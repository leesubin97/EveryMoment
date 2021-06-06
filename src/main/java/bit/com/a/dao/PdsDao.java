package bit.com.a.dao;

import java.util.List;

import bit.com.a.dto.PdsDto;

public interface PdsDao {

	List<PdsDto> getPdsList();
	
	boolean uploadPds(PdsDto dto);
	
	PdsDto getPds(int seq);
	
	void readcount(int seq);
	
	boolean updatePds(PdsDto dto);
	
	void downcount(int seq);
}
