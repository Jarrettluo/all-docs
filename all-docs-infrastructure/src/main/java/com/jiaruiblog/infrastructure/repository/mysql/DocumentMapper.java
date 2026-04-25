package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.FileDocument;
import com.jiaruiblog.domain.entity.vo.MonthStatVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface DocumentMapper {

    void save(FileDocument fileDocument);

    void update(FileDocument fileDocument);

    long count();

    FileDocument findById(@Param("id") String id);

    List<FileDocument> findByIdList(@Param("idList") List<String> idList);

    FileDocument findByMd5(@Param("md5") String md5);

    List<FileDocument> findByPage(@Param("offset") long offset, @Param("limit") int limit);

    List<FileDocument> findByPageWithFussySearch(@Param("offset") long offset, @Param("limit") int limit, @Param("keyWord") String keyWord);

    List<FileDocument> findByUserId(@Param("userId") String userId, @Param("offset") long offset, @Param("limit") int limit);

    List<FileDocument> findByUserIdAndNameContaining(@Param("userId") String userId, @Param("name") String name, @Param("offset") long offset, @Param("limit") int limit);

    boolean delete(@Param("id") String id);

    boolean deleteByIdList(@Param("idList") List<String> idList);

    List<MonthStatVO> stats(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    List<MonthStatVO> trend(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}