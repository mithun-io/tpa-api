package com.tpa.mapper;

import com.tpa.dto.request.user.UserRequest;
import com.tpa.dto.response.user.UserResponse;
import com.tpa.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "name", target = "username")
    @Mapping(source = "mobile", target = "phoneNumber")
    @Mapping(target = "password", ignore = true)
    User toUser(UserRequest userRequest);

    @Mapping(source = "username", target = "name")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    UserResponse toUserResponse(User user);

    List<UserResponse> toUserResponses(List<User> users);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "name", target = "username")
    @Mapping(source = "mobile", target = "phoneNumber")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "gender", target = "gender")
    @Mapping(target = "password", ignore = true)
    void updateEntityFromDto(UserRequest userRequest, @MappingTarget User user);
}