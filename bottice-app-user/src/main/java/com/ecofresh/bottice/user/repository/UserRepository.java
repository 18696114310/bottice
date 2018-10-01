package com.ecofresh.bottice.user.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.ecofresh.bottice.user.entities.User;

public interface UserRepository extends MongoRepository<User, String>
{
	@Query("{?0:?1}")
	public List<User> find(String key, String value);
	
	/**
	 * 有@Query声明查询, 方法名不需要严格遵守规定
	 * 根据 手机号，和昵称搜索
	 * @param phone 	手机号
	 * @param nickname	昵称
	 * @param pageable	分页
	 * @return
	 */
    @Query("{$and: [{'$or':[{'phone':{'$regex':?0, $options:'si'}},{'Privacy.name':{'$regex':?0, $options:'si'}},{'card_no':{'$regex':?0, $options:'si'}} ]},{'userType':?1},{'state':1}]}")
	public Page<User> find(final String phone,final Integer userType, Pageable pageable);
    
    @Query("{$and: [{'$or':[{'phone':{'$regex':?0, $options:'si'}},{'cardNo':?1} ]},{'userType':?2},{'state':1}]}")
	public Page<User> find(final String phone,final Long cardNo,final Integer userType, Pageable pageable);
    
    @Query("{$and: [{'userType':?0},{'state':1}]}")
	public Page<User> find(final Integer userType,Pageable pageable);
    
    @Query("{$and: [{'$or':[{'phone':?0},{'Privacy.name':?0},{'card_no':?0} ]},{'userType':{'$in':[1,2]}},{'state':1}]}")
	public Page<User> finds(final String phone,final String userType, Pageable pageable);
    
    @Query("{$and: [{'userType':{'$in':[1,2]}},{'state':1}]}")
	public Page<User> finds(final String userType, Pageable pageable);


}
