package org.games.logic.wolf;

import org.games.logic.wolf.core.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class VotingPhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.VOTING;
    }
    private final WolfKilling ctx;
    VotingPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
    boolean test;
    private float last;
    float limit;
    final Map<String,String> livedVote = new HashMap<>();
    @Final boolean talkingAgain;

    @Override
    public void end() {
        out.println("voting,voted:"+votedUserId);
    }

    @Override
    public void begin() {
        test = false;
        last = 0;
        limit = ctx.setting.votingLimit;
        ctx.lived().forEach(u->livedVote.put(u,null));
        talkingAgain = ctx.curDayTalkingTimes==1;
        out.println("voting begin.");
    }
    List<String> abandon;
    Map<String, List<String>> voteResult;
    String votedUserId;

    private void calcVoting(){
        abandon = livedVote.entrySet()
                .stream()
                .filter(e -> Objects.isNull(e.getValue()))
                .map(Map.Entry::getKey).toList();
        Map<String, List<String>> collect = voteResult=livedVote.entrySet()
                .stream()
                .filter(e -> Objects.nonNull(e.getValue()))
                .map(f -> Map.entry(f.getValue(), f.getKey()))
                .collect(Collectors.groupingBy(Map.Entry::getKey))
                .entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey(), e.getValue().stream().map(Map.Entry::getValue).toList()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        out.println("voting,detail:"+collect);
        ArrayList<Map.Entry<String, List<String>>> entries = new ArrayList<>(collect.entrySet());
        entries.sort(Comparator.comparingInt(o -> o.getValue().size()));
        Consumer<String> lastWords = (uid)->{
            votedUserId = uid;
            ctx.changeState(new LastWordsPhaser(ctx,uid,()-> ctx.changeState(new WolfPhaser(ctx))));
            ctx.curDayTalkingTimes = 0;
        };
        Runnable noOneDied = ()->{
            ctx.changeState(new WolfPhaser(ctx));
            ctx.curDayTalkingTimes = 0;
        };
        switch (entries.size()){
            case 0->{
                if(talkingAgain){
                    ctx.changeState(new TalkingPhaser(ctx));
                    return;
                }
                noOneDied.run();
            }
            case 1-> lastWords.accept(entries.get(0).getKey());
            default->{
                final Map.Entry<String, List<String>> top = entries.get(entries.size() - 1);
                final Map.Entry<String, List<String>> top2 = entries.get(entries.size() - 2);
                if(Objects.isNull(ctx.sergeant)){
                    boolean sameVotedResult = top.getValue().size()==top2.getValue().size();
                    if(sameVotedResult&&talkingAgain){
                        ctx.changeState(new TalkingPhaser(ctx));
                    }else if(sameVotedResult){
                        noOneDied.run();
                    }else{
                        lastWords.accept(top.getKey());
                    }
                    return;
                }
                final String sergeant = ctx.sergeant;
                boolean sameVotedResult = top.getValue().size()==top2.getValue().size()
                            && !top.getValue().contains(sergeant)
                            && !top2.getValue().contains(sergeant);
                if(sameVotedResult&&talkingAgain){
                    ctx.changeState(new TalkingPhaser(ctx));
                }else if(sameVotedResult){
                    noOneDied.run();
                }else{
                    String who = top.getValue().contains(sergeant)
                            ?top.getKey()
                            :top2.getKey();
                    lastWords.accept(who);
                }
            }
        }
    }
    @Override
    public void update(float dt) {
        last+=dt;
        if(last>=limit||test){
            calcVoting();
        }
    }

    @Override
    public void event(int type, Object... params) {
        switch (Event.from(type)){
            case NULL -> {}
            case ACTION -> {
                if(params.length<1){
                    out.println("voting,require 1 params ");
                    return;
                }
                switch (Action.from(Integer.class.cast(params[0]))){
                    case TEST_DONE -> {
                        test = true;
                        out.println("voting test enabled");
                    }
                    case VOTING_VOTE -> {
                        if(params.length<3){
                            out.println("voting,require 1 params ");
                            return;
                        }
                        String sender = String.class.cast(params[1]);
                        if(!livedVote.containsKey(sender)){
                            out.println("voting,not allow to voting");
                            return;
                        }
                        if(Objects.nonNull(livedVote.get(sender))){
                            out.println("voting,already voted");
                            return;
                        }
                        String target = String.class.cast(params[2]);
                        if(!livedVote.containsKey(target)){
                            out.println("voting,invalid voted");
                            return;
                        }
                        livedVote.put(sender,target);
                        out.println(sender+" vote:"+target);
                    }
                }
            }
        }
    }
}
