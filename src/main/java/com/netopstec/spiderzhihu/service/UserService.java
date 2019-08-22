package com.netopstec.spiderzhihu.service;

import com.netopstec.spiderzhihu.bloomfilter.BloomFilter;
import com.netopstec.spiderzhihu.domain.User;
import com.netopstec.spiderzhihu.domain.UserRepository;
import com.netopstec.spiderzhihu.domain.elasticsearch.ZhihuUser;
import com.netopstec.spiderzhihu.domain.elasticsearch.ZhihuUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhenye 2019/7/9
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 过滤掉user表中的重复数据---每个知乎用户有唯一的url_token
     */
    public void removeDuplicateUserList() {
        log.info("开始过滤重复的知乎用户数据");
        Long largestId = userRepository.selectLargestId();
        Long id = 1L;
        BloomFilter bloomFilter = new BloomFilter();
        while (true) {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String urlToken = user.getUrlToken();
                if (!bloomFilter.contains(urlToken)) {
                    bloomFilter.add(urlToken);
                } else {
                    log.info("[{}|{}|{}]的数据有重复，需要删除该条重复数据", user.getId(), user.getName(), urlToken);
                    userRepository.deleteById(id);
                }
            }
            id++;
            if (id > largestId) {
                log.info("已经过滤了所有的重复数据");
                return;
            }
        }
    }


    @Autowired
    private ZhihuUserRepository zhihuUserRepository;

    /**
     * 从数据从DB中转移到ES中，以备用作数据分析
     */
    public void transferDataFromDBToES() {
        log.info("开始将DB中的数据导入ES中...");
        Integer offset = 0;
        Integer pageSize = 1000;
        while (true) {
            // 从DB中查到用户数据
            List<User> userList = userRepository.findByPageQuery(offset, pageSize);
            List<ZhihuUser> zhihuUserList = userList.stream().map(user -> {
                ZhihuUser zhihuUser = new ZhihuUser();
                BeanUtils.copyProperties(user, zhihuUser);
                return zhihuUser;
            }).collect(Collectors.toList());
            // 将其格式化并将其存ES中
            zhihuUserRepository.saveAll(zhihuUserList);
            if (zhihuUserList.size() < 1000) {
                log.info("所有数据[共{}条]已经传输成功...", offset + zhihuUserList.size());
                break;
            } else {
                offset += pageSize;
            }
        }

    }
}
