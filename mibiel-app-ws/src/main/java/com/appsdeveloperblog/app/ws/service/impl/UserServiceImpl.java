package com.appsdeveloperblog.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.app.ws.exception.UserServiceException;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.Utils;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	Utils utils;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public UserDto createUser(UserDto user) {
		
		UserEntity storedUserDetails = userRepository.findUserByEmail(user.getEmail());
		if(storedUserDetails!=null) throw new RuntimeException("Record already exists");
		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);
		
		String publicUserId = utils.gnerateUserId(30);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setUserId(publicUserId);
		
		storedUserDetails = userRepository.save(userEntity);
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(storedUserDetails, returnValue);
		
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findUserByEmail(email);
		if(userEntity==null) throw new UsernameNotFoundException(email);
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto getUser(String email) {
		// TODO Auto-generated method stub
		UserEntity userEntity = userRepository.findUserByEmail(email);
		if(userEntity==null) throw new UsernameNotFoundException(email);
		
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDto gerUserByUserId(String id) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findUserByUserId(id);
		
		if(userEntity==null) throw new UsernameNotFoundException("User with ID "+id+" Not Found!");
		
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDto updateUser(String id, UserDto userDto) {
		
		UserDto returnValue = new UserDto();
		UserEntity userEntity = new UserEntity();
		UserDto userData = gerUserByUserId(id);
		
		userData.setFirstname(userDto.getFirstname());
		userData.setLastname(userDto.getLastname());
		BeanUtils.copyProperties(userData, userEntity);
		
		UserEntity uodatedUserDetails = userRepository.save(userEntity);
		
		
		BeanUtils.copyProperties(uodatedUserDetails, returnValue);
		return returnValue;
	}

	@Override
	public void deleteUser(String id) {
		
		UserEntity userEntity = userRepository.findUserByUserId(id);
		if(userEntity==null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		userRepository.delete(userEntity);
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> returnValue = new ArrayList<>();
		
		if(page>0) page = page-1;
		
		Pageable pageable = PageRequest.of(page, limit);
		Page<UserEntity> pageEntity = userRepository.findAll(pageable);
		List<UserEntity> userEntity = pageEntity.getContent();
		
		for(UserEntity ut:userEntity) {
			UserDto ud = new UserDto();
			BeanUtils.copyProperties(ut, ud);
			returnValue.add(ud);
		}
		
		return returnValue;
	}

}
