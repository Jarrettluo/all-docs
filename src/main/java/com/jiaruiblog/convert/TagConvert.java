package com.jiaruiblog.convert;

import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.entity.vo.TagVO;

/**
 * <p></p>
 * edit at 2025/7/4 21:30
 *
 * @author Jarrett Luo
 * @version 1.0
 */
public interface TagConvert {

    TagVO convertToVO(Tag tag);
}
