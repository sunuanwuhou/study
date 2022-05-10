package com.qm.study.api;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/5/9 22:23
 */
public class TestService {


    public BoardQueryVO query(BoardQueryDTO boardQueryDTO) {
        //参数校验可以放在请求结构体中去做
        boardQueryDTO.check();
        //其他逻辑
        return new BoardQueryVO();
    }
}
