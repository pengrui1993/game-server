package org.games.logic.ddz;

import org.games.model.poker.Card;
import org.games.model.poker.CardsUtil;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class FightTheLandlord {
    enum State{
        INIT,PREPARING,PLAYING,OVER
    }
    State state = State.INIT;
    static final PrintStream out = System.out;
    String creatorId;
    List<String> joinedUserId = new ArrayList<>();
    Map<String,CardsInHands> usersCards = new HashMap<>();
    float last;
    float lastJoinTime;
    Runnable ticker;
    String curActiveUserId;
    String curPuttedUserId;
    final List<String> curPassedUserId = new ArrayList<>();
    float curUserStartActiveTime;
    String winnerId;
    float createdTime;
    Runnable timeoutHandler;
    String lordId;
    Combo lastCombo;
    public FightTheLandlord(){
        ticker = ()->{};
        last = 0;
    }
    public void exit(){
        state=State.OVER;
    }
    public boolean isDone(){
        return state==State.OVER;
    }
    //*************** game lifecycle
    void onCreate(){
        if(state==State.INIT){
            out.println("game crated");
            createdTime = last;
            timeoutHandler=null;
            ticker = this::onStart;
            state = State.PREPARING;
        }
    }
    float waitJoinTime;
    List<Card> lordsCards;
    float waitLordTakeCardsTime;
    void onStart(){
        if(state==State.PREPARING){
            waitJoinTime =0;
            //notify
            out.println("game preparing,wait for join timeout for 15 seconds");
            timeoutHandler = ()->{
                if(last-waitJoinTime>15){
                    out.println("join game timeout, game over");
                    state=State.OVER;
                    ticker=this::onOver;
                }
                if(last>30){
                    timeoutHandler = null;
                    state = State.OVER;
                    ticker=this::onOver;
                }
            };
            ticker = ()->{
                if(joinedUserId.size()==3&&Objects.isNull(lordsCards)) {
                    for (String s : joinedUserId) {
                        usersCards.put(s, new CardsInHands(s));
                    }
                    final List<Card> cards = CardsUtil.allCardsWithoutStart();
                    Collections.shuffle(cards);
                    final int per = 17;
                    int remain = cards.size() - per * 3;
                    while (cards.size() != remain) {
                        for (Map.Entry<String, CardsInHands> e : usersCards.entrySet()) {
                            e.getValue().add(cards.remove(0));
                        }
                    }
                    lordsCards = cards;
                    out.println("show player's cards:");
                    for (CardsInHands c : usersCards.values()) {
                        c.sort().print();
                    }
                    out.println("lord's card:"+cards);
                    out.println("please race lord");
                    waitLordTakeCardsTime = last;
                }
                if(Objects.nonNull(lordsCards)){
                    out.println("wait for race lord 5 seconds");
                    Consumer<String> assLord = (uid)->{
                        CardsInHands lord = usersCards.get(uid);
                        while(!lordsCards.isEmpty())
                            lord.add(lordsCards.remove(0));
                        lord.sort();
                        out.println("assignment lord :"+uid);
                        usersCards.values().forEach(CardsInHands::print);
                        ticker=()->{
                            if(Objects.nonNull(winnerId)){
                                //notify
                                out.println("winner:"+winnerId);
                                state=State.OVER;
                                ticker=this::onOver;
                            }
                        };
                        curActiveUserId = uid;
                        lastCombo = Combo.ZERO;
                        state = State.PLAYING;
                        timeoutHandler = ()->{
                            //当当前玩家操作超时的时候 处理超时逻辑 TODO
                        };
                        out.println("game playing,wait for action:"+lordId);
                    };
                    timeoutHandler = ()->{
                        if(last-this.waitLordTakeCardsTime>5){
                            final ThreadLocalRandom r = ThreadLocalRandom.current();
                            final List<String> ids = joinedUserId;
                            out.println("random lord:");
                            assLord.accept(lordId = ids.get(r.nextInt(ids.size())));
                        }
                    };
                    ticker = ()->{
                        if(Objects.nonNull(lordId)){
                            assLord.accept(lordId);
                        }
                    };
                }
            };

        }
    }
    void onUpdate(float dt){
        last+=dt;
        Optional.ofNullable(timeoutHandler).ifPresent(Runnable::run);
        ticker.run();
    }
    void onOver(){
        //notify game result
        out.println("game over");
    }
    void onDestroy(){
        out.println("game destroy");
    }

    //***************** user action
    void onJoined(String joinUserId){
        if(state==State.PREPARING){
            waitJoinTime = last;
            lastJoinTime = last;
            if(!joinedUserId.contains(joinUserId)){
                joinedUserId.add(joinUserId);
                //notify
                out.println(joinUserId+" joined the game");
            }else{
                out.println(joinUserId+" already joined that game");
            }
        }
    }
    void onLeft(String leaveUserId){
        if(state==State.PREPARING) {
            if(joinedUserId.contains(leaveUserId)){
                joinedUserId.remove(leaveUserId);
                //notify
                out.println(leaveUserId+" left done");
            }else{
                out.println(leaveUserId+" not existed the game");
            }
        }
    }

    //user game action
    final Map<String,Float> senderLastSendTimes = new HashMap<>();
    void onMessage(String senderUserId,String message){
        if("help".equals(senderUserId)){

            return;
        }
        Float v = senderLastSendTimes.get(senderUserId);
        if(Objects.isNull(v)||(last-v)>5){
            //notify message
            out.println(message);
            senderLastSendTimes.put(senderUserId,last);
        }
    }
    void onPut(String putUserId,List<Card> cards){
        boolean isCur = Objects.equals(putUserId,curActiveUserId);
        if(!isCur)return;
        if(cards.isEmpty())return;
        Combo n = Combo.from(cards);
        if(!n.isValid())return;
        if(n.isBiggerThen(lastCombo)){
            //notify
            curPuttedUserId = putUserId;
            usersCards.get(putUserId).remove(n.cards);
            lastCombo = n;
            curActiveUserId = nextUserId(curPuttedUserId);
        }else{
            out.println("invalid put by user id:"+putUserId);
        }
    }
    void onPass(String actionUserId){
        boolean isCur = Objects.equals(actionUserId,curActiveUserId);
        if(!isCur)return;
        curPassedUserId.add(actionUserId);
        if (curPassedUserId.size()==2){//repeat put out
            curActiveUserId = curPuttedUserId;
            curPassedUserId.clear();
            curUserStartActiveTime = last;
            out.println("no anyone put,continue put your self any thing");
            //notify
        }else{//someone passed ,wait next action
            curUserStartActiveTime = last;
            curActiveUserId = nextUserId(actionUserId);
            //notify
        }
    }
    String nextUserId(String curUser){
        int i=0;
        for (String s : joinedUserId) {
            if(Objects.equals(s,curUser)){
                break;
            }
            i++;
        }
        i = (i+1)%joinedUserId.size();
        return joinedUserId.get(i);
    }
    public static List<Card> parseCards(CardsInHands h,Object... cards){
        if(cards.length<1)return Collections.emptyList();
        List<Card> cc = new ArrayList<>(cards.length);
        List<Card> cs = new ArrayList<>();
        final Consumer<Integer> get = (v)->{
            cs.clear();
            h.queryByCardVal(v,cs);
            if(cs.isEmpty())return;
            cc.add(cs.get(0));
        };
        for (Object card : cards) {//3 4 5 6 7 8 9 0 j k q a 2 r b
            String line = card.toString().toLowerCase(Locale.ROOT);
            for (char c : line.toCharArray()) {
                if(c>='3'&&c<='9'){
                    get.accept(c-'0');
                }else{
                    switch (c){
                        case '0':{get.accept(Card.V10);}break;
                        case 'j':{get.accept(Card.VJ);}break;
                        case 'q':{get.accept(Card.VQ);}break;
                        case 'k':{get.accept(Card.VK);}break;
                        case 'a':{get.accept(Card.VA);}break;
                        case '2':{get.accept(Card.V2);}break;
                        case 'r':{get.accept(Card.VRJ);}break;
                        case 'b':{get.accept(Card.VBJ);}break;
                    }
                }
            }
        }
        return cc;
    }
    void onAction(int action,String actionUserId,Object... params){
        Action a = Action.from(action);
        switch (a){
            case MESSAGE -> onMessage(actionUserId,String.class.cast(params[0]));
            case PUT -> onPut(actionUserId,parseCards(usersCards.get(actionUserId),params));
            case PASS -> onPass(actionUserId);
            case JOIN -> onJoined(actionUserId);
            case LEFT -> onLeft(actionUserId);
            case RACE -> onRace(actionUserId,Boolean.parseBoolean(String.class.cast(params[0])));
            default->{
                List<String> list = Arrays.stream(Action.values()).map(e->e.ordinal()+":"+ e).toList();
                out.println(String.join("\n",list));
            }
        }
    }
    Map<String,Boolean> raceLordMap = new HashMap<>();
    private void onRace(String actionUserId, boolean yes) {
        Boolean his = raceLordMap.get(actionUserId);
        if(Objects.nonNull(his)){
            if(yes){
                out.println(actionUserId+" request race lord ,but already "+his);
            }else{
                if(his){
                    raceLordMap.put(actionUserId,false);
                    out.println(actionUserId+" cancel racing lord");
                }else{
                    out.println(actionUserId+" already canceled racing lord");
                }
            }
        }else{
            raceLordMap.put(actionUserId,yes);
            out.println(actionUserId+" race lord:"+yes);
        }
        if(raceLordMap.size()==joinedUserId.size()){
            Optional<Map.Entry<String, Boolean>> any = raceLordMap.entrySet()
                    .stream()
                    .filter(Map.Entry::getValue)
                    .findAny();
            int idx = ThreadLocalRandom.current().nextInt(joinedUserId.size());
            lordId = joinedUserId.get(idx);
            any.ifPresent(e-> {
                out.println("assign lord:");
                lordId = e.getKey();
            });
        }
    }
    enum Action{
        UNKNOWN,MESSAGE,RACE,JOIN,LEFT,PUT,PASS
        ;
        static Action from(int action){
            for (Action value : Action.values())
                if(value.ordinal()==action)
                    return value;
            return UNKNOWN;
        }
    }

    static long now(){return System.currentTimeMillis();}
    static void sleep(long t){
        if(t<=0)return;
        try {
            Thread.sleep(t);
        } catch (InterruptedException ignore) {}
    }

    static class Input extends Thread{
        Input(FightTheLandlord lord, Queue<Runnable> queue){
            this.lord = lord;
            this.queue = queue;
            setDaemon(true);
            start();
        }
        final FightTheLandlord lord;
        final Queue<Runnable> queue;
        static final PrintStream out = System.out;

        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            while(!lord.isDone()){
                String line = scanner.nextLine().trim();
                if("quit".equals(line)){ lord.exit();continue;}
                final String[] a =line.split("\\s+");
                if(a.length==1){lord.onAction(0,"");continue;}
                int action;
                try{
                    action = Integer.parseInt(a[0]);
                }catch (Throwable t){
                    t.printStackTrace(out);
                    lord.onAction(0,"");
                    continue;
                }
                final String who = a[1];
                Object[] params = new Object[a.length-2];
                System.arraycopy(a,2,params,0,params.length);
                queue.offer(()-> lord.onAction(action,who,params));
            }
        }
    }
    static final Queue<Runnable> queue = new LinkedList<>();
    public static void main(String[] args) {
        FightTheLandlord lord = new FightTheLandlord();
        lord.onCreate();
        long last = now();
        List<Runnable> ls = List.of(
            ()->lord.onAction(Action.JOIN.ordinal(),"zs")
            ,()->lord.onAction(Action.JOIN.ordinal(),"ls")
            ,()->lord.onAction(Action.JOIN.ordinal(),"ww")
            ,()->lord.onAction(Action.RACE.ordinal(),"ww","true")
            ,()->lord.onAction(Action.RACE.ordinal(),"zs","false")
            ,()->lord.onAction(Action.RACE.ordinal(),"ls","false")
            ,()->lord.onAction(Action.PUT.ordinal(),"ww","34567")
        )
      ;
        queue.addAll(ls);
        Input input = new Input(lord, queue);
        while(!lord.isDone()){
            final long tmp = now();
            final float dt = (tmp-last)/1000.f;
            last = tmp;
            lord.onUpdate(dt);
            try{
                Optional.ofNullable(queue.poll()).ifPresent(Runnable::run);
            }catch (Throwable t){
                t.printStackTrace(out);
            }
            sleep(50);
        }
        lord.onDestroy();
        try {
            input.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
