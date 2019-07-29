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

import java.util.ArrayList;
import java.util.List;
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
        Integer offset = 0;
        Integer pageSize = 1000;
        BloomFilter bloomFilter = new BloomFilter();
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("线程阻塞异常...");
            }
            List<User> userList = userRepository.findByPageQuery(offset, pageSize);
            List<User> duplicateUserList = new ArrayList<>();
            for (User user : userList) {
                if (bloomFilter.contains(user.getUrlToken())) {
                    duplicateUserList.add(user);
                }
                bloomFilter.add(user.getUrlToken());
            }
            if (duplicateUserList.size() > 0) {
                log.info("在user表[{}-{}]之间有重复的待删除数据条数：[{}]", offset, offset + pageSize, duplicateUserList.size());
                userRepository.deleteInBatch(duplicateUserList);
            }
            if (userList.size() < pageSize) {
                log.info("已经对所有知乎用户的所有数据进行了去重...");
                break;
            }
            offset += pageSize;
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
