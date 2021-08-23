package com.faw.harbor.service;

import java.util.List;

/**
 * @Author Ron
 * @create 2021-1-8 13:30
 */
public interface HarborRequest {
    /**
     * 通过项目id获取所有镜像
     * @return
     */
    List<String> queryImagesByProjectId(Integer projectId);

    /**
     * 通过镜像名获取所有镜像标签
     * @return
     */
    List<String> queryImagesTagsByImageName(String imageName);

    /**
     * 通过仓库名，tag名删除镜像
     * @param repositoryName
     * @param tagName
     */
    void deleteImageTagsByName(String repositoryName, String tagName);
}

