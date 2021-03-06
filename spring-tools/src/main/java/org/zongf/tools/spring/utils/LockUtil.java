package org.zongf.tools.spring.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zongf.tools.common.exception.AbsBaseException;
import org.zongf.tools.common.exception.LockException;
import org.zongf.tools.spring.lock.ILocker;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/** 应用锁工具类
 * @author zongf
 * @date 2020-05-15
 */
public class LockUtil {

    private static Logger log = LoggerFactory.getLogger(LockUtil.class);

    // 默认锁
    private static final ILocker DEFAULT_LOCKER;

    static {
        DEFAULT_LOCKER = SpringContextUtil.getBean(ILocker.class);
    }

    /**加锁执行方法: 无参数无返回值
     * @see LockUtil#execute(org.zongf.tools.spring.lock.ILocker, java.lang.String, long, java.lang.Runnable)
     * @author zongf
     * @date 2020-06-28
     */
    public static <T> void execute(String lockName, long lockTime, Runnable runnable) {
        execute(DEFAULT_LOCKER, lockName, lockTime, runnable);
    }

    /**加锁执行方法: 无参数有返回值
     * @see LockUtil#execute(org.zongf.tools.spring.lock.ILocker, java.lang.String, long, java.util.function.Supplier)
     * @author zongf
     * @date 2020-06-28
     */
    public static <T> T execute(String lockName, long lockTime, Supplier<T> supplier) {
        return execute(DEFAULT_LOCKER, lockName, lockTime, supplier);
    }

    /**加锁执行方法: 有一个参数无返回值
     * @see LockUtil#execute(org.zongf.tools.spring.lock.ILocker, java.lang.String, long, java.util.function.Consumer, java.lang.Object)
     * @author zongf
     * @date 2020-06-28
     */
    public static <T> void execute(String lockName, long lockTime, Consumer<T> consumer, T param) {
        execute(DEFAULT_LOCKER, lockName, lockTime, consumer, param);
    }

    /**加锁执行方法: 有一个参数有返回值
     * @see LockUtil#execute(org.zongf.tools.spring.lock.ILocker, java.lang.String, long, java.util.function.Function, java.lang.Object)
     * @author zongf
     * @date 2020-06-28
     */
    public static <T, R> R execute(String lockName, long lockTime, Function<T, R> function, T param) {
        return execute(DEFAULT_LOCKER, lockName, lockTime, function, param);
    }

    /** 执行无参数无返回值方法
     * @param runnable 目标方法
     * @param locker 锁
     * @param lockName 锁名称
     * @param lockTime 锁定时间, 单位秒,  过期自动释放锁
     * @author zongf
     * @date 2020-05-15
     */
    public static <T> void execute(ILocker locker, String lockName, long lockTime, Runnable runnable) {

        try {

            boolean lockSuccess = locker.lock(lockName, lockTime);

            if (lockSuccess) {
                runnable.run();
            }else {
                throw new LockException("获取锁失败");
            }
        } catch (AbsBaseException bex) {
            throw bex;
        } catch (Exception ex) {
            throw new LockException("加锁执行异常");
        } finally {
            try {
                locker.unlock(lockName);
            } catch (Exception ex) {
                log.warn("释放锁失败, 锁名称:" + lockName);
            }
        }
    }

    /** 执行无参数无返回值方法
     * @param supplier 目标方法
     * @param locker 锁
     * @param lockName 锁名称
     * @param lockTime 锁定时间, 单位秒,  过期自动释放锁
     * @author zongf
     * @date 2020-05-15
     */
    public static <T> T execute(ILocker locker, String lockName, long lockTime, Supplier<T> supplier) {

        try {

            boolean lockSuccess = locker.lock(lockName, lockTime);

            if (lockSuccess) {
                return supplier.get();
            }else {
                throw new LockException("获取锁失败");
            }
        } catch (AbsBaseException bex) {
            throw bex;
        } catch (Exception ex) {
            throw new LockException("加锁执行异常");
        } finally {
            try {
                locker.unlock(lockName);
            } catch (Exception ex) {
                log.warn("释放锁失败, 锁名称:" + lockName);
            }
        }
    }

    /** 执行执行单参无返回值方法
     * @param consumer 目标方法
     * @param param 目标方法参数
     * @param locker 锁
     * @param lockName 锁名称
     * @param lockTime 锁定时间, 单位秒,  过期自动释放锁
     * @return R
     * @author zongf
     * @date 2020-05-15
     */
    public static <T> void execute(ILocker locker, String lockName, long lockTime, Consumer<T> consumer, T param) {

        try {

            boolean lockSuccess = locker.lock(lockName, lockTime);

            if (lockSuccess) {
                consumer.accept(param);
            }else {
                throw new LockException("获取锁失败");
            }
        } catch (AbsBaseException bex) {
            throw bex;
        } catch (Exception ex) {
            throw new LockException("加锁执行异常");
        } finally {
            try {
                locker.unlock(lockName);
            } catch (Exception ex) {
                log.warn("释放锁失败, 锁名称:" + lockName);
            }
        }
    }


    /** 加锁执行单参有返回值方法
     * @param function 目标方法
     * @param param 目标方法参数
     * @param locker 锁
     * @param lockName 锁名称
     * @param lockTime 锁定时间, 单位秒,  过期自动释放锁
     * @return R
     * @author zongf
     * @date 2020-05-15
     */
    public static <T, R> R execute(ILocker locker, String lockName, long lockTime, Function<T, R> function, T param) {

        try {

            boolean lockSuccess = locker.lock(lockName, lockTime);

            if (lockSuccess) {
                return function.apply(param);
            }else {
                throw new LockException("获取锁失败");
            }
        } catch (AbsBaseException bex) {
            throw bex;
        } catch (Exception ex) {
            throw new LockException("加锁执行异常");
        } finally {
            try {
                locker.unlock(lockName);
            } catch (Exception ex) {
                log.warn("释放锁失败, 锁名称:" + lockName);
            }
        }
    }

}
