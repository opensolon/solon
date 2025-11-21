package features.rx;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.noear.solon.rx.Completable;
import org.noear.solon.rx.SimpleSubscriber;
import org.noear.solon.rx.impl.CompletableEmitterImpl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CompletableImpl 单元测试
 */
class CompletableImplTest {

    private static final RuntimeException TEST_ERROR = new RuntimeException("Test error");

    // 用于捕获并断言信号的简单订阅者实现
    private class TestSubscriber<T> extends SimpleSubscriber<T> {
        private final AtomicBoolean completed = new AtomicBoolean(false);
        private final AtomicReference<Throwable> error = new AtomicReference<>();

        @Override
        public void onError(Throwable throwable) {
            error.set(throwable);
            completed.set(true);
        }

        @Override
        public void onComplete() {
            completed.set(true);
        }

        public void assertCompleted() {
            assertTrue(completed.get(), "Completable should have completed.");
            assertNull(error.get(), "Completable should not have errored.");
        }

        public void assertError(Throwable expected) {
            assertTrue(completed.get(), "Completable should have finished (errored).");
            assertEquals(expected, error.get(), "Completable should have errored with the expected exception.");
        }

        public void assertNotFinished() {
            assertFalse(completed.get(), "Completable should NOT have finished.");
        }
    }

    // --- 基础创建测试 ---

    @Test
    @DisplayName("Test_Static_Create_Completes")
    void testStaticCreate_Completes() {
        AtomicBoolean emitted = new AtomicBoolean(false);

        Completable completable = Completable.create(emitter -> {
            emitted.set(true);
            emitter.onComplete();
        });

        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        completable.subscribe(subscriber);

        assertTrue(emitted.get(), "The emitter consumer should have been run.");
        subscriber.assertCompleted();
    }

    @Test
    @DisplayName("Test_Static_Create_Errors")
    void testStaticCreate_Errors() {
        AtomicBoolean emitted = new AtomicBoolean(false);

        Completable completable = Completable.create(emitter -> {
            emitted.set(true);
            emitter.onError(TEST_ERROR);
        });

        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        completable.subscribe(subscriber);

        assertTrue(emitted.get(), "The emitter consumer should have been run.");
        subscriber.assertError(TEST_ERROR);
    }

    @Test
    @DisplayName("Test_Static_Complete")
    void testStaticComplete() {
        Completable completable = Completable.complete();

        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        completable.subscribe(subscriber);

        subscriber.assertCompleted();
    }

    @Test
    @DisplayName("Test_Static_Error")
    void testStaticError() {
        Completable completable = Completable.error(TEST_ERROR);

        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        completable.subscribe(subscriber);

        subscriber.assertError(TEST_ERROR);
    }

    // --- 操作符 doOnError 测试 ---

    @Test
    @DisplayName("Test_doOnError_onSuccess")
    void testDoOnError_onSuccess() {
        AtomicBoolean errorHandled = new AtomicBoolean(false);

        Completable completable = Completable.complete()
                .doOnError(err -> errorHandled.set(true));

        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        completable.subscribe(subscriber);

        subscriber.assertCompleted();
        assertFalse(errorHandled.get(), "doOnError should NOT be executed on complete.");
    }

    @Test
    @DisplayName("Test_doOnError_onError")
    void testDoOnError_onError() {
        AtomicReference<Throwable> caughtError = new AtomicReference<>();

        Completable completable = Completable.error(TEST_ERROR)
                .doOnError(err -> caughtError.set(err));

        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        completable.subscribe(subscriber);

        subscriber.assertError(TEST_ERROR);
        assertEquals(TEST_ERROR, caughtError.get(), "doOnError should catch the error.");
    }

    // --- 操作符 doOnComplete 测试 ---

    @Test
    @DisplayName("Test_doOnComplete_onComplete")
    void testDoOnComplete_onComplete() {
        AtomicInteger counter = new AtomicInteger(0);

        Completable completable = Completable.complete()
                .doOnComplete(counter::incrementAndGet);

        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        completable.subscribe(subscriber);

        subscriber.assertCompleted();
        assertEquals(1, counter.get(), "doOnComplete should be executed exactly once.");
    }

    @Test
    @DisplayName("Test_doOnComplete_onError")
    void testDoOnComplete_onError() {
        AtomicBoolean completed = new AtomicBoolean(false);

        Completable completable = Completable.error(TEST_ERROR)
                .doOnComplete(() -> completed.set(true));

        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        completable.subscribe(subscriber);

        subscriber.assertError(TEST_ERROR);
        assertFalse(completed.get(), "doOnComplete should NOT be executed on error.");
    }

    // --- 操作符 then 测试 ---

    @Test
    @DisplayName("Test_then_SuccessChain")
    void testThen_SuccessChain() {
        AtomicInteger step = new AtomicInteger(0);

        Completable c1 = Completable.create(emitter -> {
            step.set(1);
            emitter.onComplete();
        });

        Completable c2 = Completable.create(emitter -> {
            assertEquals(1, step.get());
            step.set(2);
            emitter.onComplete();
        });

        Completable completable = c1.then(c2);

        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        completable.subscribe(subscriber);

        subscriber.assertCompleted();
        assertEquals(2, step.get(), "Both completables should have executed in order.");
    }

    @Test
    @DisplayName("Test_then_ErrorInFirst")
    void testThen_ErrorInFirst() {
        AtomicInteger step = new AtomicInteger(0);

        Completable c1 = Completable.create(emitter -> {
            step.set(1);
            emitter.onError(TEST_ERROR);
        });

        Completable c2 = Completable.create(emitter -> {
            step.set(2); // Should not execute
            emitter.onComplete();
        });

        Completable completable = c1.then(c2);

        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        completable.subscribe(subscriber);

        subscriber.assertError(TEST_ERROR);
        assertEquals(1, step.get(), "Second completable should NOT have executed.");
    }

    @Test
    @DisplayName("Test_then_ErrorInSecond")
    void testThen_ErrorInSecond() {
        AtomicInteger step = new AtomicInteger(0);
        RuntimeException error2 = new RuntimeException("Second error");

        Completable c1 = Completable.complete()
                .doOnComplete(() -> step.set(1));

        Completable c2 = Completable.create(emitter -> {
            assertEquals(1, step.get());
            emitter.onError(error2);
        });

        Completable completable = c1.then(c2);

        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        completable.subscribe(subscriber);

        subscriber.assertError(error2);
        assertEquals(1, step.get(), "First completable should complete before the second starts.");
    }

    // --- 操作符 doOnErrorResume 测试 ---

    @Test
    @DisplayName("Test_doOnErrorResume_RecoveryToComplete")
    void testDoOnErrorResume_RecoveryToComplete() {
        AtomicReference<Throwable> caughtError = new AtomicReference<>();
        AtomicBoolean resumedCompleted = new AtomicBoolean(false);

        Completable resumable = Completable.create(emitter -> {
            caughtError.set(TEST_ERROR);
            emitter.onComplete();
        });

        Completable completable = Completable.error(TEST_ERROR)
                .doOnErrorResume(err -> {
                    assertEquals(TEST_ERROR, err);
                    return resumable.doOnComplete(() -> resumedCompleted.set(true));
                });

        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        completable.subscribe(subscriber);

        // 最终的流应该完成，而不是错误
        subscriber.assertCompleted();
        assertTrue(resumedCompleted.get(), "The resumed Completable should have completed.");
    }

    @Test
    @DisplayName("Test_doOnErrorResume_RecoveryToError")
    void testDoOnErrorResume_RecoveryToError() {
        RuntimeException recoveryError = new RuntimeException("Recovery error");

        Completable completable = Completable.error(TEST_ERROR)
                .doOnErrorResume(err -> {
                    assertEquals(TEST_ERROR, err);
                    return Completable.error(recoveryError);
                });

        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        completable.subscribe(subscriber);

        // 最终的流应该被恢复流的错误信号终止
        subscriber.assertError(recoveryError);
    }

    // --- 订阅方法测试 ---

    @Test
    @DisplayName("Test_subscribe_NoArg")
    void testSubscribe_NoArg() {
        // SimpleSubscriber 的默认实现是空操作。需要一个能捕获信号的 SimpleSubscriber。
        AtomicBoolean completed = new AtomicBoolean(false);

        Completable completable = Completable.create(emitter -> emitter.onComplete())
                .doOnComplete(() -> completed.set(true));

        // 这里的 subscribe() 会使用内部的 subscriberBuilder（SimpleSubscriber<Object>）
        // 并通过 doOnComplete 确保逻辑被执行。
        completable.subscribe();

        // 由于 CompletableImpl 的实现，doOnComplete 注册到了内部的 subscriberBuilder
        // 且 subscribe() 方法会触发这个流。
        assertTrue(completed.get(), "subscribe() should trigger the stream and complete.");
    }

    @Test
    @DisplayName("Test_subscribe_WithEmitter")
    void testSubscribe_WithEmitter() {
        AtomicBoolean outerCompleted = new AtomicBoolean(false);
        AtomicReference<Throwable> outerError = new AtomicReference<>();

        Completable completable = Completable.error(TEST_ERROR);

        completable.subscribe(new CompletableEmitterImpl(new TestSubscriber<Void>() {
            @Override
            public void onComplete() {
                outerCompleted.set(true);
            }

            @Override
            public void onError(Throwable throwable) {
                outerError.set(throwable);
            }
        }));

        assertFalse(outerCompleted.get());
        assertEquals(TEST_ERROR, outerError.get());
    }
}