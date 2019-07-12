package com.netopstec.spiderzhihu.service;

import com.netopstec.spiderzhihu.bloomfilter.BloomFilter;
import com.netopstec.spiderzhihu.domain.User;
import com.netopstec.spiderzhihu.domain.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        Integer offset = 0;
        Integer pageSize = 1000;
        BloomFilter bloomFilter = new BloomFilter();
        while (true) {
            List<User> userList = userRepository.findByPageQuery(offset, pageSize);
            List<User> duplicateUserList = new ArrayList<>();
            for (User user : userList) {
                if (bloomFilter.contains(user.getUrlToken())) {
                    duplicateUserList.add(user);
                }
                bloomFilter.add(user.getUrlToken());
            }
            if (duplicateUserList.size() > 0) {
                log.info("在user表[{}-{}]之间有重复的待删除数据条数：[]", offset, offset + pageSize, duplicateUserList.size());
                userRepository.deleteInBatch(duplicateUserList);
            }
            if (userList.size() < pageSize) {
                log.info("已经对所有知乎用户的所有数据进行了去重...");
                break;
            }
            offset += pageSize;
        }
    }
}
