package weshare.controller;

import io.javalin.http.Handler;
import weshare.model.Expense;
import weshare.model.MoneyHelper;
import weshare.model.PaymentRequest;
import weshare.persistence.ExpenseDAO;
import weshare.server.ServiceRegistry;

import javax.money.MonetaryAmount;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class paymentRequestController {

    public static Handler paymentRequest = context -> {
        ExpenseDAO expenseDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Expense expense = expenseDAO.get(UUID.fromString(Objects.requireNonNull(context.queryParam("id")))).get();
        Collection<PaymentRequest> listOfPaymentRequests = expense.listOfPaymentRequests();
        MonetaryAmount finalAmount = getNett(listOfPaymentRequests);
        MonetaryAmount lessAmount = amountAfterPaymentRequestSent(expense);
        context.attribute("expense",expense);
        context.attribute("listOfPaymentRequests", listOfPaymentRequests);
        context.attribute("finalAmount",finalAmount);
        context.attribute("lessAmount",lessAmount);
        context.render("paymentRequest.html");
    };

    public static MonetaryAmount getNett(Collection<PaymentRequest> listOfPaymentRequests){
        int amount = 0;
        for (PaymentRequest request: listOfPaymentRequests) {
            amount += request.getAmountToPay().getNumber().intValueExact();
        }
        return MoneyHelper.amountOf(amount);

    }

    public static MonetaryAmount amountAfterPaymentRequestSent(Expense expense){
        return expense.getAmount().subtract(expense.totalAmountOfPaymentsRequested());

    }


}
