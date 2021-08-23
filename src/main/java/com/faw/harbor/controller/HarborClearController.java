package com.faw.harbor.controller;

import com.faw.harbor.dtos.DeleteInfo;
import com.faw.harbor.service.HarborRequest;
import com.faw.harbor.service.impl.HarborRequestServiceImpl;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author luhaifeng
 */
@RequestMapping(value = "/clearHarbor")
@RestController
public class HarborClearController {

    private static final Logger log = LoggerFactory.getLogger(HarborClearController.class);
    @Autowired
    private HarborRequest harborRequest;

    private Integer projectId = 2;

    //private String repoName = "iap_docker/springboot_alert";

    public static void main(String[] args) {
        String dateStr = "20170721213".substring(0,8);
        LocalDate tagDate = LocalDate.parse(dateStr, DateTimeFormatter.BASIC_ISO_DATE);
        System.out.println(tagDate);
    }

    @GetMapping(value = "/doClearHarborByProjectIdAndRepoName")
    public void doClearHarborByProjectIdAndRepoName()
    {
        List<String> repositories = harborRequest.queryImagesByProjectId(projectId);
        List<String> deleteRepos = new ArrayList<>(64);
        for (String rep : repositories) {
            try {
                List<String> tags = harborRequest.queryImagesTagsByImageName(rep);
                int cnt = tags.size();
                if(tags.size()>100){
                    System.out.println();
                    System.out.println("=========================================");
                    System.out.println(rep);
                    deleteRepos.add(rep);
                    LocalDate oneYearAgo = LocalDate.now().plusYears(-1);
                    //保留日期在一年内的标签，不能转化为日期的标签不处理
                    for (int i = 0; i < tags.size(); i++) {
                        String tag = tags.get(i);
                        try {
                            String dateStr = tag.substring(0,8);
                            LocalDate tagDate = LocalDate.parse(dateStr, DateTimeFormatter.BASIC_ISO_DATE);
                            //把超出100个且时间在1年以前的标签删掉
                            if(tagDate.isBefore(oneYearAgo) && cnt > 100)
                            {
                                System.out.println("delete repo:{"+ rep+"}, tag:{" + tag + "}");
                                harborRequest.deleteImageTagsByName(rep, tag);
                                cnt--;
                            }
                        }
                        catch (Exception e)
                        {
                            continue;
                        }
                    }

                }
            } catch (Exception e) {
                log.error("an error occurred while deleting the repository: {}, error: {}",
                        rep, e);
            }
        }
    }

    @GetMapping(value = "/doClearByRepoName")
        public void doClearByRepoName(@RequestParam String repoName)
    {
        List<String> tags = harborRequest.queryImagesTagsByImageName(repoName);
        log.info("tags count: {}", tags.size());

        //保留100个标签
        if (tags.size() > 100) {
            for (int i = 0; i < tags.size() - 100; i++) {
                String tag = tags.get(i);
                System.out.println("delete repo:{"+ repoName+"}, tag:{" + tag + "}");
                //log.info("delete repo:{}, tag:{}", repoName, tag);
                harborRequest.deleteImageTagsByName(repoName, tag);
            }

        }
    }

    @GetMapping("/test")
    public void test1()
    {
        for (int i = 1; i <= 19; i++) {
            String url = "http://10.161.12.90:8443/api/projects/2/logs/filter?page=" + i +"&page_size=1000";

            //设置header和认证
            HttpHeaders headers = HarborRequestServiceImpl.createHeaders("app","111aaaBBB");

            //拼接参数和header
            JsonObject param = new JsonObject();
            param.addProperty("begin_timestamp",1629043200);
            param.addProperty("end_timestamp", 1629129599);
            param.addProperty("keywords", "delete");
            param.addProperty("project_id", 2);
            param.addProperty("username", "app");
            HttpEntity<String> httpEntity = new HttpEntity<>(param.toString(),headers);

            //重定义restTemplate
            RestTemplate restTemplate = new RestTemplate();

            //提交请求
            ResponseEntity<List<DeleteInfo>> resp = restTemplate.exchange(url, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<List<DeleteInfo>>(){});
            if (resp.getStatusCode().value() == 200){
                resp.getBody().forEach(deleteInfo -> {
                    System.out.println(deleteInfo.getRepo_tag());
                });
            }else {
                log.info("####Harbor镜像【{}】镜像标签获取失败：",resp);

            }
        }
        String url = "http://10.161.12.90:8443/api/projects/2/logs/filter?page=1&page_size=1000";

        //设置header和认证
        HttpHeaders headers = HarborRequestServiceImpl.createHeaders("app","111aaaBBB");

        //拼接参数和header
        JsonObject param = new JsonObject();
        param.addProperty("begin_timestamp",1629043200);
        param.addProperty("end_timestamp", 1629129599);
        param.addProperty("keywords", "delete");
        param.addProperty("project_id", 2);
        param.addProperty("username", "app");
        HttpEntity<String> httpEntity = new HttpEntity<>(param.toString(),headers);

        //重定义restTemplate
        RestTemplate restTemplate = new RestTemplate();

        //提交请求
        ResponseEntity<List<DeleteInfo>> resp = restTemplate.exchange(url, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<List<DeleteInfo>>(){});
        if (resp.getStatusCode().value() == 200){

        }else {
            log.info("####Harbor镜像【{}】镜像标签获取失败：",resp);

        }

    }


}
