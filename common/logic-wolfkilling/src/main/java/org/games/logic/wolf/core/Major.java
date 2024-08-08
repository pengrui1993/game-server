package org.games.logic.wolf.core;

public enum Major {
    PREPARING,WOLF,WITCH,PREDICTOR,PROTECTOR, CALC_DIED,RACE,HUNTER
    ,DIED_INFO,LAST_WORDS,ORDERING,TALKING,VOTING
    ,OVER,DONE;

    public static Major from(int code) {
        for (Major value : values()) {
            if(value.ordinal()==code)return value;
        }
        return null;
    }
}
/*
#include<iostream>
#include<functional>
#include<thread>
#include<chrono>
#include<memory>
#include<mutex>
#include<queue>
enum class EventType:int{
	Tick
	
};

struct Event{
virtual EventType type()const=0;
};
class EventQueue{
public:
	using Item = std::shared_ptr<Event>;
private:
	using Queue = std::queue<Item>;
	using Mutex = std::mutex;
	using Locker = std::lock_guard<Mutex>;
	Queue queue;
	Mutex mtx;
public:
	//EventQueue():queue(),mtx(){}
	void push(Item itm){
		Locker locker(mtx);
		queue.push(itm);
	}
	Item pop(){
		Locker locker(mtx);
		if(queue.empty())
			//return nullptr;
			return Item(nullptr);
		auto itm = queue.front();
		queue.pop();
		return itm;
	}
};
struct TickEvent:public Event{
	virtual EventType type() const override{
		return EventType::Tick;
	}
	
};

class WolfKill{
	using Callback = std::function<void(Event*)>;
	Callback cur;
	bool running;
public:
	WolfKill():cur([](Event* dt){
		std::cout << "init" << std::endl;
	}),running(true){}
	void event(Event* evt){
		if(!evt)return;
		if(!cur)return;
		cur(evt);
	}
	bool isRunning(){return running;}
};

EventQueue::Item getItem(){
	return nullptr;
}
std::ostream& operator<<(std::ostream& o,EventType type){
	o << (int)type;
	return o;
}
int main(){
	EventQueue::Item test;
	if(test=getItem()){
		std::cout << test->type() << std::endl;
	}
	EventQueue queue;
	queue.push(std::make_shared<TickEvent>());
	WolfKill wf;
	while(wf.isRunning()){
		std::this_thread::sleep_for(std::chrono::seconds(1));
		EventQueue::Item itm;
		while(itm=queue.pop()){
			auto& evt = *itm;
			wf.event(&evt);
		}

		
	}
	
	return 0;
}
*/