package com.msa.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.msa.domain.Follow;
import com.msa.repository.FollowRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class FollowServiceImpl implements FollowService {
	
	private Logger logger = LoggerFactory.getLogger(FollowServiceImpl.class);

	@Autowired
	FollowRepository followRepository;
	
	@HystrixCommand
	@Override
	public Follow addFollow(Long followeeId, Long followerId) {
		
		Follow follow = new Follow(followeeId, followerId);
		
		Follow result = followRepository.saveAndFlush(follow);
		
		return result;
	}

	@HystrixCommand
	@Override
	public List<Follow> getFollowerList(Long followeeId) {

		List<Follow> followList = followRepository.findByFolloweeId(followeeId);
		
		return followList;
	}

	@Override
	@Transactional
	public void deleteFollow(Long followeeId, Long followerId) {
		followRepository.deleteByFolloweeIdAndFollowerId(followeeId, followerId);

	}

	@Override
	public List<Follow> getFolloweeList(Long followerId, List<Long> userIdList) {
		logger.info("FollowServiceImpl getFolloweeList()");
		
		List<Follow> followList = followRepository.findByFollowerIdAndFolloweeIdIn(followerId, userIdList);
		
		return followList;
	}

}
