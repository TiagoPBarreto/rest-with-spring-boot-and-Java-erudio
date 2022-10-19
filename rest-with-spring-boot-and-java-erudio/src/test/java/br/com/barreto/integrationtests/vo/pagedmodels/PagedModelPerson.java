package br.com.barreto.integrationtests.vo.pagedmodels;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.barreto.integrationtests.vo.PersonVO;

@XmlRootElement
public class PagedModelPerson {
	
	@XmlElement(name="content")
	private List<PersonVO>content;

	public PagedModelPerson() {}

	public List<PersonVO> getContent() {
		return content;
	}

	public void setContent(List<PersonVO> content) {
		this.content = content;
	}
	
}
