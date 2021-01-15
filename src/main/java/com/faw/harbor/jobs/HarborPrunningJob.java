package com.faw.harbor.jobs;

import com.faw.harbor.dtos.ImagesTags;
import com.faw.harbor.dtos.Repositories;
import com.faw.harbor.service.HarborRequest;
import com.faw.harbor.dtos.HarborProject;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Ron
 * @create 2021-1-8 13:25
 */
@Service
public class HarborPrunningJob extends QuartzJobBean {

    @Autowired
    private HarborRequest harborRequest;

    private static final Logger log = LoggerFactory.getLogger(HarborPrunningJob.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        log.info("========== Begin to pruning the expired tags ===========");
        executeImageTagPruning();
        log.info("========== Complete ===========");
    }

    private void executeImageTagPruning() {
        List<HarborProject> projects = harborRequest.queryProjects();
        for (HarborProject project : projects) {
            log.info("----Begin to check the project name: {}", project.getName());
            List<Repositories> repositories = harborRequest.queryImagesByProjectId(project.getProject_id());
            for (Repositories rep : repositories) {
                try {
                    log.info("----Begin to check the repository, name: {}", rep.getName());
                    List<ImagesTags> tags = harborRequest.queryImagesTagsByImageName(rep.getName());
                    log.info("tags count: {}", tags.size());
                    if(tags.size()<6){
                        log.info("tags count <=5, continue, repository name: {}", rep.getName());
                        continue;
                    }
                    for (ImagesTags tg : tags) {
                        //取出成yyyy-MM-dd格式
                        String dt = tg.getCreated().substring(0, 10);
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        tg.setCreatedDate(df.parse(dt));
                    }
                    List<ImagesTags> sortedTags = tags.stream().sorted(Comparator.comparing
                            (ImagesTags::getCreatedDate).reversed()).collect(Collectors.toList());
                    if (sortedTags.size() > 5) {
                        List<ImagesTags> leftTags = sortedTags.subList(5, sortedTags.size());
                        log.info("{} tags left", leftTags.size());
                        Calendar c = Calendar.getInstance();
                        //过去15天
                        c.setTime(new Date());
                        c.add(Calendar.DATE, -15);
                        Date last15day = c.getTime();
                        for (ImagesTags leftTag : leftTags) {
                            if (leftTag.getCreatedDate().getTime() < last15day.getTime()) {
                                log.info("tag " + leftTag.getName() + " to be deleted, created time: " + leftTag.getCreated());
                                harborRequest.deleteImageTagsByName(rep.getName(), leftTag.getName());
                            } else {
                                log.info("tag " + leftTag.getName() + " not to be deleted,created time: " + leftTag.getCreated());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("an error occurred while deleting the repository: {}, error: {}",
                            rep.getName(), e);
                }
            }
        }
    }
}