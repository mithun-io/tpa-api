package com.tpa.mapper;

import com.tpa.dto.request.UserRequest;
import com.tpa.dto.response.UserResponse;
import com.tpa.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-10T09:00:40+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserRequest userRequest) {
        if ( userRequest == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.address( userRequest.getAddress() );
        user.dateOfBirth( userRequest.getDateOfBirth() );
        user.email( userRequest.getEmail() );
        user.gender( userRequest.getGender() );
        user.mobile( userRequest.getMobile() );
        user.password( userRequest.getPassword() );
        user.username( userRequest.getUsername() );

        return user.build();
    }

    @Override
    public UserResponse toUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setAddress( user.getAddress() );
        userResponse.setCreatedAt( user.getCreatedAt() );
        userResponse.setDateOfBirth( user.getDateOfBirth() );
        userResponse.setEmail( user.getEmail() );
        userResponse.setGender( user.getGender() );
        userResponse.setId( user.getId() );
        userResponse.setMobile( user.getMobile() );
        userResponse.setUserRole( user.getUserRole() );
        userResponse.setUserStatus( user.getUserStatus() );
        userResponse.setUsername( user.getUsername() );

        return userResponse;
    }

    @Override
    public List<UserResponse> toUserResponses(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserResponse> list = new ArrayList<UserResponse>( users.size() );
        for ( User user : users ) {
            list.add( toUserResponse( user ) );
        }

        return list;
    }
}
