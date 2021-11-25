package com.qee.cloud.demo.provider;


import org.qee.cloud.rpc.api.annotation.CloudService;

@CloudService(id = "userServiceImplCloudService")
public class UserServiceImpl implements UserService {
    @Override
    public String getUser(long userId) {
        return "name:" + userId;
    }
}