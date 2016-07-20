package br.com.afirmanet.core.bean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@Data
@ToString(exclude = "nodes")
@NoArgsConstructor
@AllArgsConstructor
public class FileNode implements Serializable {

	private static final long serialVersionUID = -3601371436745263977L;

	private String name;
	private String path;
	private LocalDateTime lastModified;
	private String size;
	@JsonSubTypes(value = { @Type(value = FileNode.class) })
	private List<FileNode> nodes;

}