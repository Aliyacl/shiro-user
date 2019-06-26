package com.cl.shirouser.service.impl;

import com.cl.shirouser.common.ServerResponse;
import com.cl.shirouser.dao.DeptMapper;
import com.cl.shirouser.dao.UserMapper;
import com.cl.shirouser.entity.Dept;
import com.cl.shirouser.entity.User;
import com.cl.shirouser.service.IDeptService;
import com.cl.shirouser.util.TreeUtil;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("deptService")
public class DeptServiceImpl implements IDeptService {

    @Autowired
    private TransportClient client;
    @Autowired
    private DeptMapper deptMapper;
    @Autowired
    private UserMapper userMapper;

    public ServerResponse list(List<Integer> deptIdList){
//        List<Integer> allDeptIds = new ArrayList<>();
//        allDeptIds.addAll(deptIdList);
//        for (Integer d:deptIdList){
//            List<Integer> tempDeptId = new ArrayList<>();
//            tempDeptId= getChildDepts(d,tempDeptId);
//            allDeptIds.addAll(tempDeptId);
//        }
        List<Dept> deptList = deptMapper.getDepts(deptIdList);
        if(deptList.size()==1){
            return ServerResponse.createBySuccess(deptList);
        }else{
            List<Dept> deptTree = TreeUtil.toTree(deptList,"deptId","parentId","children",Dept.class);
            return ServerResponse.createBySuccess(deptTree);
        }
    }

    public List<Integer> getChildDepts(Integer deptId,List<Integer> deptIds){
        List<Dept> childDepts = deptMapper.getChildDept(deptId);
        for(Dept dept:childDepts){
            deptIds.add(dept.getDeptId());
            getChildDepts(dept.getDeptId(),deptIds);
        }
        return deptIds;
    }

    public ServerResponse edit(Dept dept){
       int rowCount =  deptMapper.updateByPrimaryKeySelective(dept);
       if(rowCount>0){
           return ServerResponse.createBySuccess("修改部门成功");
       }
       return ServerResponse.createByError();
    }

    public ServerResponse add(Dept dept){
        int rowCount =  deptMapper.insert(dept);
        if(rowCount>0){
            return ServerResponse.createBySuccess("新建部门成功");
        }
        return ServerResponse.createByError();
    }

    public ServerResponse del(Integer deptId){
        List<Dept> childDepts = deptMapper.getChildDept(deptId);
        if(childDepts!=null&&childDepts.size()!=0){
            return ServerResponse.createByError("请先删除此部门下的子部门");
        }
        List<User> userList = userMapper.getUserByDeptId(deptId);
        if(userList!=null&&userList.size()!=0){
            return ServerResponse.createByError("请先删除此部门下的成员");
        }
        int rowCount =  deptMapper.deleteByPrimaryKey(deptId);
        if(rowCount>0){
            return ServerResponse.createBySuccess("删除部门成功");
        }
        return ServerResponse.createByError();
    }

    public ServerResponse search(String keyword){
        BoolQueryBuilder boolQuery1 = QueryBuilders.boolQuery();
        BoolQueryBuilder boolQuery2 = QueryBuilders.boolQuery();
        if(keyword!=null){
            boolQuery1.must(QueryBuilders.matchQuery("userName",keyword));
            boolQuery2.must(QueryBuilders.matchQuery("deptName",keyword));
        }
        SearchRequestBuilder builder1 = this.client.prepareSearch("user")
                .setTypes("user")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQuery1)
                .setFrom(0)
                .setSize(10);
        SearchRequestBuilder builder2 = this.client.prepareSearch("dept")
                .setTypes("dept")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQuery2)
                .setFrom(0)
                .setSize(10);
        System.out.println(builder1);
        System.out.println(builder2);
        SearchResponse response1 = builder1.get();
        SearchResponse response2 = builder2.get();
        List<Map<String,Object>> result1 = new ArrayList<Map<String,Object>>();
        for(SearchHit hit:response1.getHits()){
            result1.add(hit.getSourceAsMap());
        }
        List<Map<String,Object>> result2 = new ArrayList<Map<String,Object>>();
        for(SearchHit hit:response2.getHits()){
            result2.add(hit.getSourceAsMap());
        }
        Map<String,Object> result = new HashMap<>();
        result.put("user",result1);
        result.put("dept",result2);
        return ServerResponse.createBySuccess(result);
    }
}
