package com.zrc.springcloud.domain;

/**
 * Created with IntelliJ IDEA.
 * User: ZRC
 * Date Time: 2019/8/20 9:21
 * Description: No Description
 */

public class User {
    /**
     * ID
     */
    private Long id;

    /**
     * 用户名称
     */
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
