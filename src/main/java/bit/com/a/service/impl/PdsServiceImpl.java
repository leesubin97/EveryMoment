package bit.com.a.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bit.com.a.dao.PdsDao;
import bit.com.a.dto.CalendarDto;
import bit.com.a.dto.PdsDto;
import bit.com.a.service.PdsService;

@Service
public class PdsServiceImpl implements PdsService {

	@Autowired
	PdsDao dao;

	@Override
	public List<PdsDto> getPdsList() {		
		return dao.getPdsList();
	}

	@Override
	public boolean uploadPds(PdsDto dto) {		
		return dao.uploadPds(dto);		
	}

	@Override
	public boolean updatePds(PdsDto dto) {		
		return dao.updatePds(dto);
	}
	
	@Override
	public void readcount(int seq) {
		dao.readcount(seq);
	}

	@Override
	public PdsDto getPds(int seq) {		
		return dao.getPds(seq);
	}
	
	@Override
	public void downcount(int seq) {
		dao.downcount(seq);		
	}
	
}
