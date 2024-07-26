package org.wolf.race;

import org.wolf.action.Action;
import org.wolf.evt.Event;

import java.util.*;

class VotingPhaser extends MinorPhaser{
    private final Context ctx;
    final List<String> raceUp;
    final List<String> raceDown;
    //down vote up/null
    final Map<String,String> voteResult = new HashMap<>();
    final boolean first;
    public VotingPhaser(Context ctx, List<String> raceUp, List<String> raceDown) {
        this.ctx = ctx;
        this.raceUp = raceUp;
        this.raceDown = raceDown;
        this.first = ctx.cur().getClass()== FirstSpeechingPhaser.class;;
    }
    @Override
    public Minor state() {
        return Minor.VOTING;
    }

    @Override
    public void begin() {
        last = 0;
        out.println("voting,start vote");
        test = false;
    }
    boolean test;
    float last;
    @Override
    public void update(float dt) {
        last+=dt;
        if((last>=10&&voteResult.size()!=raceDown.size())||test){//timeout
            for (String s : raceDown) {
                if(!voteResult.containsKey(s))voteResult.put(s,null);
            }
            assert voteResult.size()==raceDown.size();
        }
        if(voteResult.size()==raceDown.size()){
            out.println(voteResult);
            final List<String> reject = new ArrayList<>();
            final Map<String,List<String>> counter = new HashMap<>();
            for (Map.Entry<String, String> e : voteResult.entrySet()) {
                if(e.getValue()==null){
                    reject.add(e.getKey());
                    continue;
                }
                List<String> res = counter.get(e.getValue());
                if(Objects.isNull(res)){
                    res = new ArrayList<>();
                    counter.put(e.getValue(),res);
                }
                res.add(e.getKey());
            }
            out.println("pass:"+reject);
            out.println("vote:"+counter);
            Runnable noSergeant =()->{
                if(first) ctx.changeState(new SecondSpeechingPhaser(ctx,raceUp,raceDown));
                else ctx.changeState(new DonePhaser(ctx,null));
            };
            if(counter.isEmpty()){
                noSergeant.run();
            }else if(counter.size()==1){
                ctx.changeState(new DonePhaser(ctx,counter.keySet().iterator().next()));
            }else{
                List<Map.Entry<String, List<String>>> list = new ArrayList<>(counter.entrySet().stream().toList());
                list.sort(Comparator.comparingInt(o -> o.getValue().size()));
                Map.Entry<String, List<String>> l1 = list.get(list.size() - 1);
                if(l1.getValue().size()==list.get(list.size()-2).getValue().size()){
                    noSergeant.run();
                }else{
                    ctx.changeState(new DonePhaser(ctx,l1.getKey()));
                }
            }
        }
    }

    @Override
    public void event(int type, Object... params) {
        Event event = Event.from(type);
        switch (event){
            case ACTION -> {
                if(params.length<1){
                    out.println("voting require params more then one");
                    return;
                }
                Action a = Action.from(Integer.class.cast(params[0]));
                switch (a){
                    case RACE_VOTE->{
                        if(params.length<3){
                            out.println("voting, require 3 params");
                            return;
                        }
                        final String voter = String.class.cast(params[1]);
                        if(!raceDown.contains(voter)){
                            out.println(voter+" no permission to vote");
                            return;
                        }
                        final String voted = String.class.cast(params[2]);
                        if(!raceUp.contains(voted)){
                            out.println(voted+" no permission to be voted");
                            return;
                        }
                        voteResult.put(voter,voted);
                        //notify
                        out.println("voting ok,"+voter+" voted:"+voted);
                    }
                    case TEST_DONE->{
                        out.println("voting test enabled");
                        test = true;
                    }
                }
            }
        }
    }
}
