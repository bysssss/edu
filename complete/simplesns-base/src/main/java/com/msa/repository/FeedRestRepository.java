package com.msa.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.msa.domain.Feed;
import com.msa.repository.dto.FeedData;
import com.msa.repository.dto.FeedRequestDto;
import com.msa.repository.dto.FeedResponseDto;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@Repository
public class FeedRestRepository {
	
	private Logger logger = LoggerFactory.getLogger(FeedRestRepository.class);
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	DiscoveryClient discoveryClient;

	/*@HystrixCommand(
			commandProperties= {
				@HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="10")
			}
		)*/
	/*@HystrixCommand(
			commandProperties= {
				@HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="10"),
				@HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value="10"),
				@HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="5")
			},
			fallbackMethod = "getFeedListFallBack"
		)*/
	@HystrixCommand(
			commandKey = "feed",
			fallbackMethod = "getFeedListFallBack"
		)
	public List<Feed> getFeedList(Long userId) {
		List<ServiceInstance> instanceList = discoveryClient.getInstances("socialservice");
		logger.info(instanceList.get(0).getUri().toString());
		
		//FeedResponseDto response = restTemplate.getForEntity("http://localhost:8081/feed?userId={userId}", FeedResponseDto.class, userId).getBody();
		//FeedResponseDto response = restTemplate.getForEntity(instanceList.get(0).getUri().toString() + "/feed?userId={userId}", FeedResponseDto.class, userId).getBody();
		FeedResponseDto response = restTemplate.getForEntity("http://socialservice/feed?userId={userId}", FeedResponseDto.class, userId).getBody();

		List<FeedData> feedDataList = response.getData();
		List<Feed> feedList = new ArrayList<>();
		for (FeedData data : feedDataList) {
			feedList.add(new Feed(data.getUserId(), data.getFolloweeId(), data.getPostId()));
		}

		return feedList;
	}
	public List<Feed> getFeedListFallBack(Long userId) {
		List<Feed> feedList = new ArrayList<>();
		
		feedList.add(new Feed(userId, 57L, 233L));
		feedList.add(new Feed(userId, 57L, 198L));
		
		return feedList;
	}
	
	@HystrixCommand(
			commandKey = "feed"
		)
	public void addFeeds(Long userId, Long postId) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		
		FeedRequestDto requestDto = new FeedRequestDto(userId, postId);
		
		HttpEntity<FeedRequestDto> entity = new HttpEntity<FeedRequestDto>(requestDto, headers);

		//restTemplate.exchange("http://localhost:8081/feed", HttpMethod.POST, entity, String.class);
		restTemplate.exchange("http://socialservice/feed", HttpMethod.POST, entity, String.class);
	}
}
