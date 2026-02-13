package com.xueliandan.gulimall.gulisearch.service;

import com.xueliandan.gulimall.gulisearch.vo.SearchParam;
import com.xueliandan.gulimall.gulisearch.vo.SearchResp;

public interface IMallSearchService {
    SearchResp search(SearchParam searchParam);
}
