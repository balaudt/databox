package com.sagarius.goddess.server;

import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;
import org.restlet.service.TunnelService;

import com.sagarius.goddess.server.resources.AcademicYearResource;
import com.sagarius.goddess.server.resources.AttendanceResource;
import com.sagarius.goddess.server.resources.ClassUpdateResource;
import com.sagarius.goddess.server.resources.DocumentResource;
import com.sagarius.goddess.server.resources.ExamResource;
import com.sagarius.goddess.server.resources.MessageTemplateResource;
import com.sagarius.goddess.server.resources.NewExamResource;
import com.sagarius.goddess.server.resources.ParentResource;
import com.sagarius.goddess.server.resources.PhotoResource;
import com.sagarius.goddess.server.resources.RegisterResource;
import com.sagarius.goddess.server.resources.SMSResource;
import com.sagarius.goddess.server.resources.base.AuthSubRestlet;
import com.sagarius.goddess.server.resources.base.LoginResource;
import com.sagarius.goddess.server.resources.base.MappingResource;
import com.sagarius.goddess.server.resources.base.SchoolResource;
import com.sagarius.goddess.server.resources.base.UtilityRestlet;
import com.sagarius.goddess.server.resources.mapreduce.BTotalUpdateResource;
import com.sagarius.goddess.server.resources.mapreduce.ConsolidateAttendGen;
import com.sagarius.goddess.server.resources.mapreduce.ConsolidateResource;
import com.sagarius.goddess.server.resources.mapreduce.NewMonthAttendanceGen;
import com.sagarius.goddess.server.resources.mapreduce.NewQueuedExamResource;
import com.sagarius.goddess.server.resources.mapreduce.QueuedAttendanceResource;
import com.sagarius.goddess.server.resources.mapreduce.QueuedExamResource;
import com.sagarius.goddess.server.resources.mapreduce.QueuedSMSResource;
import com.sagarius.goddess.server.resources.mapreduce.QueuedYearResource;
import com.sagarius.goddess.server.resources.mapreduce.StudentUpdateResource;
import com.sagarius.goddess.server.resources.parent.StudentResource;

public class RootApplication extends Application {
	@Override
	public Restlet createInboundRoot() {
		TunnelService tunnelService = getTunnelService();
		tunnelService.setEnabled(true);
		tunnelService.setMethodTunnel(true);
		tunnelService.setMethodParameter("m");
		tunnelService.setQueryTunnel(true);

		Router resourceRouter = new Router(getContext());
		resourceRouter.attach("/login", LoginResource.class);
		resourceRouter.attach("/authsub", new AuthSubRestlet());
		resourceRouter.attach("/school", SchoolResource.class);
		resourceRouter.attach("/year", AcademicYearResource.class);
		resourceRouter.attach("/documents", DocumentResource.class);
		resourceRouter.attach("/photo", PhotoResource.class);
		resourceRouter.attach("/sms", SMSResource.class);
		resourceRouter.attach("/exam", ExamResource.class);
		resourceRouter.attach("/nexam", NewExamResource.class);
		resourceRouter.attach("/attendance", AttendanceResource.class);
		resourceRouter.attach("/mtemplate", MessageTemplateResource.class);
		resourceRouter.attach("/parent", ParentResource.class);
		resourceRouter.attach("/parent/student", StudentResource.class);
		resourceRouter.attach("/parent/student/{studentId}",
				StudentResource.class);
		resourceRouter
				.attach("/parent/attendance/{studentId}",
						com.sagarius.goddess.server.resources.parent.AttendanceResource.class);
		resourceRouter
				.attach("/parent/exam/{docId}/{studentId}",
						com.sagarius.goddess.server.resources.parent.ExamResource.class);
		resourceRouter.attach("/qsms", QueuedSMSResource.class);
		resourceRouter.attach("/qyear", QueuedYearResource.class);
		resourceRouter.attach("/qexam", QueuedExamResource.class);
		resourceRouter.attach("/nqexam", NewQueuedExamResource.class);
		resourceRouter.attach("/qe_btotal", BTotalUpdateResource.class);
		resourceRouter.attach("/qe_student", StudentUpdateResource.class);
		resourceRouter.attach("/qe_class", ClassUpdateResource.class);
		resourceRouter.attach("/qe_consolidate", ConsolidateResource.class);
		resourceRouter.attach("/qattendance", QueuedAttendanceResource.class);
		resourceRouter.attach("/qattendmonth", NewMonthAttendanceGen.class);
		resourceRouter.attach("/qattendconsolid", ConsolidateAttendGen.class);

		Filter filter = new Filter(getContext(), resourceRouter) {
			@Override
			protected int beforeHandle(Request request, Response response) {
				return CONTINUE;
			}
		};
		Router baseRouter = new Router(getContext());
		baseRouter.attach("/util", new UtilityRestlet());
		baseRouter.attach("/admin/map", MappingResource.class);
		baseRouter.attach("/register", RegisterResource.class);
		baseRouter.attachDefault(filter);
		return baseRouter;
	}
}
