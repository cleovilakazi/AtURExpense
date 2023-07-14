package weshare.controller;

import   io.javalin.http.Handler;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.jetbrains.annotations.NotNull;
import weshare.model.Expense;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static weshare.model.MoneyHelper.ZERO_RANDS;
import static weshare.model.MoneyHelper.amountOf;

public class ExpensesController {


    public static final Handler view = context -> {

        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);
        MonetaryAmount nett = getNett(expenses);
        Map<String, Object> viewModel = new java.util.HashMap<>(Map.of("expenses", expenses));
        viewModel.put("nett",nett);
        viewModel.put("zero", ZERO_RANDS);
        context.render("expenses.html", viewModel);
    };

    public static MonetaryAmount getNett(Collection<Expense> expenses){
        int init = 0;
        for(Expense expense : expenses){
            init += expense.amountLessPaymentsReceived().getNumber().intValueExact();
        }
        return amountOf(init);

    }




}
