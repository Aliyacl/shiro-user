package com.cl.shirouser.controller;

import com.cl.shirouser.common.ServerResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class WebController {
    @Autowired
    private TransportClient client;

    /*
    elasticSearch简单查询
    */
    @GetMapping(value = "get_person_man")
    @ResponseBody
    public ServerResponse get(@RequestParam(name="id",defaultValue = "")String id) {
        if(id.isEmpty()){
            return ServerResponse.createByError("参数为空");
        }
        GetResponse result = this.client.prepareGet("person","man",id).get();
        if(!result.isExists()){
            return ServerResponse.createByError("结果不存在");
        }
        return ServerResponse.createBySuccess(result.getSource());
    }

    /*
    elasticSearch增加
    */
    @PostMapping(value = "add_person_man")
    @ResponseBody
    public ServerResponse add(@RequestParam(name="name")String name,
                              @RequestParam(name="country")String country,
                              @RequestParam(name="age")int age,
                              @RequestParam(name="date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        try{
            XContentBuilder content = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("name",name)
                    .field("country",country)
                    .field("age",age)
                    .field("date",date.getTime())
                    .endObject();
            IndexResponse result = this.client.prepareIndex("person","man")
                    .setSource(content)
                    .get();
            return ServerResponse.createBySuccess(result.getId());
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByError("不成功");
        }
    }

    /*
    elasticSearch删除
    */
    @DeleteMapping(value = "delete_person_man")
    @ResponseBody
    public ServerResponse del(@RequestParam(name="id")String id){
        DeleteResponse result = this.client.prepareDelete("person","man",id)
                    .get();
            return ServerResponse.createBySuccess(result.getResult().toString());
    }

    /*
    elasticSearch修改
    */
    @PostMapping(value = "update_user")
    @ResponseBody
    public ServerResponse update(@RequestParam(name="id")String id,
                              @RequestParam(name="userName")String userName,
                              @RequestParam(name="deptName",required = false)String deptName){
        UpdateRequest update = new UpdateRequest("user","user",id);
        try {
            XContentBuilder content = XContentFactory.jsonBuilder()
                    .startObject();
            if(userName!=null){
                content.field("userName",userName);
            }
            content.endObject();
            update.doc(content);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByError("不成功");
        }
        try{
            UpdateResponse result = this.client.update(update).get();
            return ServerResponse.createBySuccess(result.getResult().toString());
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByError("不成功");
        }
    }

    /*
    elasticSearch复合查询
    */
    @PostMapping(value = "query_person_man")
    @ResponseBody
    public ServerResponse query(@RequestParam(name="name",required = false)String name,
                                @RequestParam(name="country",required = false)String country,
                                @RequestParam(name="gt_age",defaultValue = "0")int gtAge,
                                @RequestParam(name="lt_age",required = false)Integer ltAge) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if(name!=null){
            boolQuery.must(QueryBuilders.matchQuery("name",name));
        }
        if(country!=null){
            boolQuery.must(QueryBuilders.matchQuery("country",country));
        }
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("age")
                .from(gtAge);
        if(ltAge!=null&&ltAge>0){
            rangeQuery.to(ltAge);
        }
        boolQuery.filter(rangeQuery);
        SearchRequestBuilder builder = this.client.prepareSearch("person")
                .setTypes("man")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQuery)
                .setFrom(0)
                .setSize(10);
        System.out.println(builder);
        SearchResponse response = builder.get();
        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
        for(SearchHit hit:response.getHits()){
            result.add(hit.getSourceAsMap());
        }
        return ServerResponse.createBySuccess(result);
    }
}
