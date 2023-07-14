package weshare.controller;

import io.javalin.http.Handler;
import weshare.model.Expense;
import weshare.model.MoneyHelper;
import weshare.model.PaymentRequest;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.Routes;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.*;

import static weshare.model.DateHelper.TODAY;

public class PRRController {

    public static final Handler view = context -> {
        int totalReceivedRequests = 0;
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);
        Collection<PaymentRequest> requestsReceived = expensesDAO.findPaymentRequestsReceived(personLoggedIn);
        for (PaymentRequest request : requestsReceived) {
            totalReceivedRequests += request.getAmountToPay().getNumber().intValue();


        }
        MonetaryAmount totalRequestsSentAmountConverted = MoneyHelper.amountOf(totalReceivedRequests);
        Map<String, Object> viewModel = new HashMap<>(Map.of("payments", requestsReceived));
        viewModel.put("grand_total",totalRequestsSentAmountConverted);
        context.render("paymentRequestsReceived.html", viewModel);
        System.out.println(requestsReceived);
    };

    public static final Handler requestsReceived = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);
        Collection<PaymentRequest> requestsReceived = expensesDAO.findPaymentRequestsReceived(personLoggedIn);
        UUID id = UUID.fromString(Objects.requireNonNull(context.formParam("expenseId")));
        Expense expense = expensesDAO.get(id).get();
        PaymentRequest request = getRelevantExpense(requestsReceived,id);
        id = request.getId();
        Person person = request.getPersonWhoShouldPayBack();
        LocalDate date = request.getDueDate();
        expense.payPaymentRequest(id,person,TODAY);
        MonetaryAmount amount = request.getAmountToPay();
        String description = request.getDescription();
        expensesDAO.save(new Expense(personLoggedIn, description,amount,TODAY));
        context.redirect(Routes.PRR);
    };

    public static PaymentRequest getRelevantExpense(Collection<PaymentRequest> paymentRequestCollection, UUID id){
        PaymentRequest r = null;
        for (PaymentRequest request: paymentRequestCollection) {
            if(request.getExpenseId().equals(id)){
                r = request;
            }
        }
        return r;

    }


}
