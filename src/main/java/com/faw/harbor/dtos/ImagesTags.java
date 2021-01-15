package com.faw.harbor.dtos;

import java.util.Date;

/**
 * @Author Ron
 * @create 2021-1-8 13:31
 */
public class ImagesTags{
    private String name;
    private String created;
    private Date createdDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

}
