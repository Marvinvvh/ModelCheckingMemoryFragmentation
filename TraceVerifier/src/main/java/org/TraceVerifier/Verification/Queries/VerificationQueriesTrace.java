package org.TraceVerifier.Verification.Queries;

import org.TraceVerifier.Query.FailOnCompilationQuery;
import org.TraceVerifier.Query.PassFailTraceQuery;
import org.TraceVerifier.Query.TraceQueryCollection;

/**
 * Class that contains all Trace-related queries.
 */
public class VerificationQueriesTrace {
    public static TraceQueryCollection SingleActionPerTick()
    {
        String identifier = "SingleActionPerTick";
        String comment = """
                For trace, we know that verify state and verify action are committed, and the only non-trap state where time passes is wait_for_action, only enterable through verify_action.
                Upon transitioning from wait_for_action into verify state, we set tick to 0 and increase the amount of clocks resets to get into verify state. Therefore, in that non-errored loop, clock_resets == timer.
                If we reach verify action, we have processed the action, i.e., moved the action to global vars to be read when the allocator_controller gets signalled.
                As action_count is the index to the array of actions, we know that if clock_resets is equal to action count, the action iterations are synchronous with timer.
                If an error is present verify action is never reached again (due to trap), so we make the query less strict.
                """;

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        String query = "A[] (trace.verify_action imply trace.clock_resets == action_count) and (trace.verify_state imply (trace.clock_resets - 1) == action_count)";
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection ExpectedAllocationAction()
    {
        String identifier = "ExpectedAllocationAction";
        String comment = """
                    A successful action processing is triggered by transitioning from verify_action into wait_for_action.
                    Processing an action is triggered by transitioning from verify_state into verify_action.
                    Therefore, if actions[action_count] == allocator_action_req[ALLOCATOR_ID], the proper action is passed to the used allocator.
                    We iterate over the allocation actions using allocator_count.
                """;

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        String query = "A[] trace.verify_action imply forall(i : int[0, ALLOCATOR_COUNT - 1])(trace.actions[action_count - 1] == allocator_action_req[i])";
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection ActionsProcessedBeforeNewAction(int time)
    {
        String identifier = "ActionsProcessedBeforeNewAction";
        String comment = "Check whenever processing an action, whether the trace doesn't attempt to enter a state that triggers a new processing signal. at time "+time+".";
        String query = "A[] (trace.wait_for_action && trace.tick == "+time+") imply (not allocators_processing && not trace.fault_allocators_processing)";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CheckBoundedInvalidAllocationActionsError() {
        String identifier = "CheckBoundsInvalidAllocationActionsError";
        String comment = """
                    Check whether the bounds on the allocation actions in the trace actually stop compiling the model.
                    The query itself doesn't really have an effect, but if it DID pass it would check whether we arrive in the error state.
                """;
        // Note that this query doesn't matter when checking a trace with an invalid action, because we have bounds. So the query in that scenario doesn't really matter. It does matter for a valid trace, however.
        String query = "A[] (trace.wait_for_action && trace.clock_resets < ALLOC_ACTION_COUNT) imply (!trace.fault_invalid_trace && (trace.actions[action_count].type == eAllocatorActionAlloc || trace.actions[action_count].type == eAllocatorActionFree))";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new FailOnCompilationQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CheckBoundedInvalidAllocationActionsOK() {
        String identifier = "CheckBoundedInvalidAllocationActionsOK";
        String comment = """
                    Check whether we never enter a state where the trace is attempting to process an action while other models are still busy processing.
                """;
        String query = "A[] (trace.wait_for_action && trace.clock_resets < ALLOC_ACTION_COUNT) imply (!trace.fault_invalid_trace && (trace.actions[action_count].type == eAllocatorActionAlloc || trace.actions[action_count].type == eAllocatorActionFree))";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection ExpectedActionCountInDone()
    {
        String identifier = "ExpectedActionCountInDone";
        String comment = """
                Checks whether the amount of clock_resets (minus one because we have one last time tick to go to the done state) matches the actions processed.
                """;
        String query = "A[] trace.done imply trace.clock_resets - 1 == action_count";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }


    public static TraceQueryCollection ProcessingStopsAfterNoAllocatorsLeft()
    {
        String identifier = "ProcessingStopsAfterNoAllocatorsLeft";
        String comment = """
                Whenever there are no allocators left to process, so either the trace is iterated over entirely, or the allocators are all errored, we never reach allocators_processing.
                """;
        String query = " A[] (not trace.wait_for_action && (trace.clock_resets == ALLOC_ACTION_COUNT || errorless_allocator_count == 0)) imply not allocators_processing";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }
}
