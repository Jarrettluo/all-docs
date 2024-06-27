package com.jiaruiblog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zys
 * @since 2024-06-27
 */
@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Role {
	@Id
	private String id;

	@NotBlank(message = "非空")
	private String roleName;

	@NotBlank(message = "非空")
	private String roleKey;

	private List<String> permIds = new ArrayList<>();

	private Date createDate;

	private Date updateDate;
}
