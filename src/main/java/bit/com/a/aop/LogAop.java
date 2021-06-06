package bit.com.a.aop;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import bit.com.a.dto.MemberDto;

@Aspect
public class LogAop {
	
	@Around("within(bit.com.a.controller.*)")
	public Object loggerAop(ProceedingJoinPoint joinpoint)throws Throwable {
		
		String signatureStr = joinpoint.getSignature().toShortString();
		
		// session check
		HttpServletRequest request 
			= ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
		
		if(request != null) {
			HttpSession session = request.getSession();
			MemberDto login = (MemberDto)session.getAttribute("login");			
			if(login == null) {
				return "redirect:/sessionOut.do";
			}
		}		
		
		// System.out.println(signatureStr + "시작");
		
		try {
			Object obj = joinpoint.proceed();	// 실행시
			
			System.out.println("loggerAOP:" + signatureStr + " 메소드가 실행 " + new Date());
			
			return obj;
			
			
		}finally {
		//	System.out.println("실행 후:" + System.currentTimeMillis());
		//	System.out.println(signatureStr + "종료");	// 실행후
		}		
	}

}









