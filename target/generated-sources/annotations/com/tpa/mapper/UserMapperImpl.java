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
    date = "2026-05-11T12:55:18+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserRequest userRequest) {
        if ( userRequest == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.username( userRequest.getUsername() );
        user.email( userRequest.getEmail() );
        user.mobile( userRequest.getMobile() );
        user.dateOfBirth( userRequest.getDateOfBirth() );
        user.address( userRequest.getAddress() );
        user.password( userRequest.getPassword() );
        user.gender( userRequest.getGender() );

        return user.build();
    }

    @Override
    public UserResponse toUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setId( user.getId() );
        userResponse.setUsername( user.getUsername() );
        userResponse.setEmail( user.getEmail() );
        userResponse.setMobile( user.getMobile() );
        userResponse.setDateOfBirth( user.getDateOfBirth() );
        userResponse.setAddress( user.getAddress() );
        userResponse.setGender( user.getGender() );
        userResponse.setUserRole( user.getUserRole() );
        userResponse.setUserStatus( user.getUserStatus() );
        userResponse.setCreatedAt( user.getCreatedAt() );

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
