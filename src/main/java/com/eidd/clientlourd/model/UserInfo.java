package com.eidd.clientlourd.model;

import java.util.List;

public record UserInfo(String username, List<String> roles) {
}
