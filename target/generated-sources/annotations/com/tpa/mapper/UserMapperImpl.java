package com.tpa.mapper;

import com.tpa.dto.request.user.UserRequest;
import com.tpa.dto.response.user.UserResponse;
import com.tpa.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-25T17:23:49+0530",
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

        user.username( userRequest.getName() );
        user.phoneNumber( userRequest.getMobile() );
        user.address( userRequest.getAddress() );
        user.dateOfBirth( userRequest.getDateOfBirth() );
        user.email( userRequest.getEmail() );
        user.gender( userRequest.getGender() );

        return user.build();
    }

    @Override
    public UserResponse toUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setName( user.getUsername() );
        userResponse.setPhoneNumber( user.getPhoneNumber() );
        userResponse.setAddress( user.getAddress() );
        userResponse.setCreatedAt( user.getCreatedAt() );
        userResponse.setDateOfBirth( user.getDateOfBirth() );
        userResponse.setEmail( user.getEmail() );
        userResponse.setGender( user.getGender() );
        userResponse.setId( user.getId() );
        userResponse.setUserRole( user.getUserRole() );
        userResponse.setUserStatus( user.getUserStatus() );

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

    @Override
    public void updateEntityFromDto(UserRequest userRequest, User user) {
        if ( userRequest == null ) {
            return;
        }

        user.setUsername( userRequest.getName() );
        user.setPhoneNumber( userRequest.getMobile() );
        user.setAddress( userRequest.getAddress() );
        user.setDateOfBirth( userRequest.getDateOfBirth() );
        user.setGender( userRequest.getGender() );
    }
}
