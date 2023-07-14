package weshare.server;

import weshare.controller.*;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public class Routes {
    public static final String LOGIN_PAGE = "/";
    public static final String LOGIN_ACTION = "/login.action";
    public static final String LOGOUT = "/logout";
    public static final String EXPENSES = "/expenses";
    public static final String PRS = "/paymentrequests_sent";
    public static final String PRR = "/paymentrequests_received";
    public static final String newExpense = "/newexpense";
    public static final String newExpenseDotAction = "/expense.action";
    public static final String payRequest = "/paymentRequest";
    public static final String PRSdotAction = "/paymentrequest.action";
    public static final String payment_action = "/payment.action";




    public static void configure(WeShareServer server) {
        server.routes(() -> {
            post(LOGIN_ACTION,              PersonController.login);
            get(LOGOUT,                     PersonController.logout);
            get(EXPENSES,                   ExpensesController.view);
            get(PRS,                        PRSController.view);
            get(PRR,                        PRRController.view);
            get(newExpense,                 newExpenseController.view);
            post(newExpenseDotAction,       newExpenseController.saveExpense);
            get(payRequest,                 paymentRequestController.paymentRequest);
            post(PRSdotAction,              PRSController.requestSent);
            post(payment_action,            PRRController.requestsReceived);
        });
    }
}
