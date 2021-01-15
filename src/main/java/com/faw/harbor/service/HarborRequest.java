package com.faw.harbor.service;

import com.faw.harbor.dtos.HarborProject;
import com.faw.harbor.dtos.HarborUser;
import com.faw.harbor.dtos.ImagesTags;
import com.faw.harbor.dtos.Repositories;

import java.util.List;

/**
 * @Author Ron
 * @create 2021-1-8 13:30
 */
public interface HarborRequest {
    /**
     * 创建Harbor用户
     * @param harborUser
     * @return
     */
    void createUser(HarborUser harborUser);

    /**
     * 创建project
     * @param harborProject
     * @return
     */
    void createProject(HarborProject harborProject, String username, String password);

    /**
     * 查询所有项目
     * @return
     */
    List<HarborProject> queryProjects();

    /**
     * 通过项目id获取所有镜像
     * @return
     */
    List<Repositories> queryImagesByProjectId(Integer projectId);

    /**
     * 通过镜像名获取所有镜像标签
     * @return
     */
    List<ImagesTags> queryImagesTagsByImageName(String imageName);

    /**
     * 通过仓库名，tag名删除镜像
     * @param repositoryName
     * @param tagName
     */
    void deleteImageTagsByName(String repositoryName, String tagName);
}

