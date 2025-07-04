package com.jiaruiblog.repository;

import com.jiaruiblog.entity.vo.MonthStatVO;

import java.util.Date;
import java.util.List;

/**
 * <p></p>
 * edit at 2025/7/4 20:56
 *
 * @author Jarrett Luo
 * @version 1.0
 */
public interface DocumentRepository {

    List<MonthStatVO> xx(Date startDate, Date endDate);
}
