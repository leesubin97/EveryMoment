package bit.com.a.controller;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import bit.com.a.dto.CalParam;
import bit.com.a.dto.CalendarDto;
import bit.com.a.dto.CalendarParam;
import bit.com.a.dto.MemberDto;
import bit.com.a.service.CalendarService;
import bit.com.a.util.UtilEx;

@Controller
public class CalendarController {

	@Autowired
	CalendarService service;
	
	@RequestMapping(value = "calendarlist.do", method = {RequestMethod.GET, RequestMethod.POST})
	public String calendarlist(Model model, CalendarParam param, HttpSession session) {		
		model.addAttribute("doc_title", "일정목록");
		
		Calendar cal = Calendar.getInstance();
		
		int year = param.getYear();
		int month = param.getMonth();
		int day = param.getDay();
		
		if(month == 0) {
			year--;
			month = 12;
		}
		else if(month == 13) {
			year++;
			month = 1;
		}
		else if(month < 0) {	// 처음 들어온 경우 여기로 들어 와서 연월일을 현재 날짜로 셋팅한다 
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH) + 1;
			if(day < 0) {
				day = cal.get(Calendar.DATE);
			}
		}		 
		cal.set(year, month - 1, 1);	// 요일을 구하기 위한 설정
				
		// 요일
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		// 마지막 날짜
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		// 셋팅된 날짜를 다시 넘겨주기 위한 set
		param.setYear(year);
		param.setMonth(month);
		param.setDay(day);
		param.setDayOfWeek(dayOfWeek);
		param.setLastDay(lastDay);
				
		// 로그인 정보
		String id = ((MemberDto)session.getAttribute("login")).getId();
		// 날짜 취득
		String yyyymm = UtilEx.yyyymm(param.getYear(), param.getMonth());
		
		// DB에서 그달의 일정을 모두 취득하기 위한 Dto
		CalendarDto fcal = new CalendarDto();
		fcal.setId(id);
		fcal.setRdate(yyyymm);
		
		// Db로부터 일정들을 취득한다
		List<CalendarDto> list = service.getCalendarList(fcal);
		
		// 짐싸!
		model.addAttribute("flist", list);	// 일정목록을 포장
		model.addAttribute("cal", param);	// 설정된 날짜를 포장
		
		return "calendarlist.tiles";
	}
	
	@RequestMapping(value="calwrite.do", method={RequestMethod.GET, RequestMethod.POST})
	public String calwrite(Model model, CalendarParam cal) {		
		model.addAttribute("doc_title", "일정쓰기");
		
		model.addAttribute("cal", cal);
		
		return "calwrite.tiles";		
	}
	// calwriteAf.do
	@RequestMapping(value="calwriteAf.do", method={RequestMethod.GET, RequestMethod.POST})
	public String calwriteAf(Model model, CalParam calparam) {
		
		String yyyyMmdd = UtilEx.yyyymmddhhmm(calparam.getYear(),
				calparam.getMonth(), calparam.getDay(),
				calparam.getHour(),calparam.getMin());	// 시간까지 포함 시켰음.
		
		CalendarDto dto = new CalendarDto(calparam.getId(),
										calparam.getTitle(),
										calparam.getContent(),
										yyyyMmdd);		
		service.writeCalendar(dto);
		
		model.addAttribute("year", calparam.getYear());
		model.addAttribute("month", calparam.getMonth());
		
		return "forward:/calendarlist.do";		
	}

	@RequestMapping(value="caldetail.do", method={RequestMethod.GET, RequestMethod.POST})
	public String caldetail(CalendarDto cal, Model model) {
		model.addAttribute("doc_title", "일정보기");
		
		CalendarDto dto = service.getDay(cal);		
		model.addAttribute("cal", dto);		
		
		return "caldetail.tiles";		
	}
	
	@RequestMapping(value = "callist.do", method = {RequestMethod.POST,RequestMethod.GET})
	public String callist(HttpServletRequest request, CalendarParam calparam, Model model) {		
		model.addAttribute("doc_title", "CALENDAR 일별일정");
		
		System.out.println("calparam:" + calparam.toString());
		
		String id=((MemberDto)request.getSession().getAttribute("login")).getId();
		String yyyyMmdd=UtilEx.yyyymmdd(calparam.getYear(), calparam.getMonth(), calparam.getDay());
		
		CalendarDto fcal=new CalendarDto(-1, id, null, null, yyyyMmdd, null);
		
		System.out.println("id:" + id);
		System.out.println("yyyyMmdd:" + yyyyMmdd);
		
		List<CalendarDto> flist = service.getDayList(fcal);
				
		model.addAttribute("callist", flist);
		
		model.addAttribute("year", calparam.getYear());
		model.addAttribute("month", calparam.getMonth());
		
		return "callist.tiles";
	}
	
	@RequestMapping(value = "calendarMonth.do", method = {RequestMethod.GET, RequestMethod.POST})
	public String callistmonth(HttpServletRequest request, Model model, int year, int month) {
		model.addAttribute("doc_title", "월별 일정 목록");
		
		MemberDto dto = (MemberDto)request.getSession().getAttribute("login");
		
		String id = dto.getId();
		
		Calendar cal = Calendar.getInstance();
		
		if(year == 0 && month == 0){
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH) + 1;
		}
			
		String rdate = Integer.toString(year) + UtilEx.two(month + "");
		
		CalendarDto caldto = new CalendarDto();
		
		caldto.setId(id);
		caldto.setRdate(rdate);;
		
		List<CalendarDto> callistmonth = service.getCallistmonth(caldto);
		
		model.addAttribute("month", month);
		model.addAttribute("year", year);
		model.addAttribute("callistmonth", callistmonth);		
		
		return "calendarMonth.tiles";
	}
	
	@RequestMapping(value="calupdate.do", method={RequestMethod.GET, RequestMethod.POST})
	public String calupdate(CalendarDto cal, Model model) {
		model.addAttribute("doc_title", "일정수정");
				
		CalendarDto dto = service.getDay(cal);
		System.out.println(dto.toString());
		model.addAttribute("cal", dto);
		
		int year = Integer.parseInt(dto.getRdate().substring(0, 4));
		int month = Integer.parseInt(dto.getRdate().substring(4, 6));
		int day = Integer.parseInt(dto.getRdate().substring(6, 8));
		int hour = Integer.parseInt(dto.getRdate().substring(8, 10));
		int min = Integer.parseInt(dto.getRdate().substring(10));
		
		CalParam cp = new CalParam();
		
		cp.setYear(year);
		cp.setMonth(month);
		cp.setDay(day);		
		cp.setHour(hour);
		cp.setMin(min);
		
		cp.setSeq(dto.getSeq());
		cp.setTitle(dto.getTitle());
		cp.setContent(dto.getContent());
		
		model.addAttribute("cp", cp);
				
		return "calupdate.tiles";		
	}
	
	@RequestMapping(value="calupdateAf.do", method={RequestMethod.GET, RequestMethod.POST})
	public String calupdate(CalParam calparam, Model model) {
		
		System.out.println(calparam.toString());
		String yyyyMmdd = UtilEx.yyyymmddhhmm(calparam.getYear(),
				calparam.getMonth(), calparam.getDay(),
				calparam.getHour(),calparam.getMin());	// 시간까지 포함 시켰음.
		
		System.out.println(yyyyMmdd);		
		CalendarDto dto = new CalendarDto(calparam.getSeq(), 
											calparam.getId(), 
											calparam.getTitle(), 
											calparam.getContent(), 
											yyyyMmdd, 
											"");		
		service.updateCalendar(dto);
		
		model.addAttribute("year", calparam.getYear());
		model.addAttribute("month", calparam.getMonth());
		
		return "forward:/calendarlist.do";
	}
}






