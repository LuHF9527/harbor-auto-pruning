package com.faw.harbor.service.impl;

import com.faw.harbor.config.HarborConfig;
import com.faw.harbor.dtos.HarborUser;
import com.faw.harbor.service.HarborRequest;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * @Author Ron
 * @create 2021-1-8 13:33
 */
@Service
public class HarborRequestServiceImpl implements HarborRequest {

    RestTemplate restTemplate;
    @Autowired
    HarborConfig harborConfig;

    private static final Logger log = LoggerFactory.getLogger(HarborRequestServiceImpl.class);

    /**
     * harbor管理员账户
     */
    private final String harborAdminUsername = "app";
    private final String harborAdminPassword = "111aaaBBB";

    @Override
    public void createUser(HarborUser harborUser) {
        //url
        String url = harborConfig.getApi() + "/users";

        //设置header和认证
        HttpHeaders headers = createHeaders(harborAdminUsername,harborAdminPassword);

        Gson gs =new Gson();
        //拼接参数和header
        HttpEntity<String> httpEntity = new HttpEntity<>(gs.toJson(harborUser), headers);

        //重定义restTemplate
        restTemplate = new RestTemplate();

        //提交请求
        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<String>(){});

        if (resp.getStatusCode().value() == 201){
            log.info("####Harbor用户【{}】创建成功",harborUser.getUsername());
        }else {
            log.info("####Harbor用户【{}】创建失败，错误信息{}：",harborUser.getUsername(),resp.getStatusCode());
        }
    }

    @Override
    public void createProject(HarborProject harborProject, String username, String password) {
        //url
        String url = harborConfig.getApi() + "/api/projects";

        //设置header和认证
        HttpHeaders headers = createHeaders(username,password);
        Gson gs =new Gson();
        //拼接参数和header
        HttpEntity<String> httpEntity = new HttpEntity<>(gs.toJson(harborProject), headers);

        //重定义restTemplate
        restTemplate = new RestTemplate();

        //提交请求
        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<String>(){});

        if (resp.getStatusCode().value() == 201){
            log.info("####Harbor项目【{}】创建成功",harborProject.getName());
        }else {
            log.info("####Harbor项目【{}】创建失败，错误信息{}：",harborProject.getName(),resp.getStatusCode());
        }
    }

    @Override
    public List<HarborProject> queryProjects() {
        restTemplate = new RestTemplate();

        //url
        String url = harborConfig.getApi() + "/api/projects";

        //设置header和认证
        HttpHeaders headers = createHeaders(harborAdminUsername,harborAdminPassword);
        headers.setContentType(MediaType.APPLICATION_JSON);
        //拼接参数和header
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        //提交请求
        ResponseEntity<List<HarborProject>> resp = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<HarborProject>>(){});

        if (resp.getStatusCode().value() == 200){
            if (resp.getBody() != null && resp.getBody().size() > 0){
               return resp.getBody();
            }
        }else {
            log.info("####Harbor项目基本信息查询失败,状态码: {}",resp.getStatusCode().value());
        }
        return null;
    }

    @Override
    public List<String> queryImagesByProjectId(Integer projectId) {
        //url
        String url = harborConfig.getApi() + "/api/repositories?page=2&page_size=1000&project_id=" + projectId;

        //设置header和认证
        HttpHeaders headers = createHeaders(harborAdminUsername,harborAdminPassword);

        //拼接参数和header
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        //重定义restTemplate
        restTemplate = new RestTemplate();

        headers.setContentType(MediaType.APPLICATION_JSON);
        //提交请求
        ResponseEntity<List<String>> resp = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<String>>(){});

        if (resp.getStatusCode().value() == 200){
            return resp.getBody();
        }else {
            log.info("####Harbor项目ID【{}】镜像列表获取失败：",projectId);
            return null;
        }

    }

    @Override
    public List<String> queryImagesTagsByImageName(String imageName) {
        //url
        String url = harborConfig.getApi() + "/api/repositories/tags?repo_name=" + imageName;

        //设置header和认证
        HttpHeaders headers = createHeaders(harborAdminUsername,harborAdminPassword);

        //拼接参数和header
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        //重定义restTemplate
        restTemplate = new RestTemplate();

        //提交请求
        ResponseEntity<List<String>> resp = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<String>>(){});
        if (resp.getStatusCode().value() == 200){
            return resp.getBody();
        }else {
            log.info("####Harbor镜像【{}】镜像标签获取失败：",imageName);
            return null;
        }

    }

    @Override
    public void deleteImageTagsByName(String repositoryName, String tagName) {
        //http://10.161.12.90:8443/api/repositories?repo_name=iap_docker%2Fspringboot_alert&tag=201801261105
        String url = harborConfig.getApi() + "/api/repositories?repo_name=" + repositoryName + "&tag="+tagName;

        //设置header和认证
        HttpHeaders headers = createHeaders(harborAdminUsername,harborAdminPassword);

        //拼接参数和header
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        //重定义restTemplate
        restTemplate = new RestTemplate();

        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, new ParameterizedTypeReference<String>(){});

        if (resp.getStatusCode().value() == 200){
            log.info("删除tag: {}成功, repository name: {}",tagName,repositoryName);
        }else {
            log.info("删除tag: {}失败, repository name: {}",tagName,repositoryName);
        }

    }

    /**
     * Authorization Basic认证
     * @return
     */
    public  static HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {
            {
                String auth = username + ":" + password;
                String authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.US_ASCII));
                set("Authorization", authHeader);
            }
        };
    }

}
