package com.stylefeng.guns.core.util;

/**
 * @author wjj
 * @version 1.0
 * @date 2019/10/30 20:33
 */
//因为令牌桶对业务有一定的容忍度
public class TokenBucket {
    //桶的容量
    private int bucketNums=100;
    //流入输入/ms
    private int rate=1;
    //当前令牌数量
    private int nowTokens=0;
    //时间
    private long timeStamp=getNowTime();

    private long getNowTime(){
        return System.currentTimeMillis();
    }

    public boolean getToken(){
        //记录来拿令牌的时间
        long nowTime=getNowTime();
        //添加令牌【判断该有多少个令牌】
        nowTokens=nowTokens+(int)(nowTime-timeStamp)*rate;
        //添加以后的令牌数量与桶的容量哪个小
        nowTokens=Math.min(bucketNums,nowTokens);
        //System.out.println("当前token数量"+nowTokens);
        //修改拿令牌的时间
        timeStamp=nowTime;
        //判断令牌是否足够
        if (nowTokens>=1){
            nowTokens--;
            return true;
        }else {
            return false;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TokenBucket tokenBucket = new TokenBucket();
        for (int i=0;i<100;i++){
            if (i==40){
                Thread.sleep(500);
            }
            System.out.println("第"+i+"次请求"+tokenBucket.getToken());
        }
    }

}
