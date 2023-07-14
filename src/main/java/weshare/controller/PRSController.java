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
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static weshare.model.MoneyHelper.amountOf;

public class PRSController {
    public static final Handler view = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);
        Collection<PaymentRequest> prs = expensesDAO.findPaymentRequestsSent(personLoggedIn);
        MonetaryAmount finalAmount = getNett(prs);
        Map<String, Object> viewModel = new java.util.HashMap<>(Map.of("prSent", prs));
        viewModel.put("finalAmount",finalAmount);
        context.render("paymentRequestsSent.html", viewModel);

    };

    public static final Handler requestSent = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);
        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);
        String email = context.formParam("email");
        String[] date = Objects.requireNonNull(context.formParam("due_date")).split("-");
        String amnt = context.formParam("amount");
        UUID id = UUID.fromString(Objects.requireNonNull(context.formParam("id")));
        Expense expense = getExpense(expenses,id);
        LocalDate localDate = LocalDate.of(Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]));
        MonetaryAmount amount = amountOf(Long.parseLong(Objects.requireNonNull(amnt)));
        Person person = new Person(email);
        expense.requestPayment(person,amount,localDate);
        context.redirect(Routes.payRequest+"?id="+id);

    };

        public static Expense getExpense(Collection<Expense> expenses, UUID id) {
        Expense expense = null;

        for (Expense ex : expenses) {
            if (ex.getId().equals(id)){
                expense = ex;
            }
        }
        return expense;
    }

    public static MonetaryAmount getNett(Collection<PaymentRequest> prs){
        int init = 0;
        for(PaymentRequest paymentRequest : prs){
            init += paymentRequest.getAmountToPay().getNumber().intValueExact();
        }
        return amountOf(init);

    }
}
