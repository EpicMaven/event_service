#!/usr/bin/env ruby
#
#
# Generates test data for the Event Service
#

require 'rest_client'
require 'time'
require 'set'
require 'json'

def epoch_millis(time)
  (time.to_f * 1000).to_i
end

def to_time(epochMillis)
  Time.at(epochMillis/1000.0).utc
end

class EventType
  attr_accessor :current_state
  attr_reader :type, :states

  def initialize(type, states, changes_per_day)
    @type = type
    @states = states
    @current_state = 0
    @changes_per_day = changes_per_day
  end

  def events(day)
    events = Array.new
    for time in random_times(day)
      events.push( {"type" => @type, "value" => next_state(), "epochMillis" => time} )
    end
    events
  end

  def random_times(day)
    midnight = epoch_millis(Time.utc(day.year, day.month, day.day, 0, 0, 0))
    times = Array.new
    for delta in rand_n(@changes_per_day).sort
      time = midnight + delta
      times.push(time)
    end
    times
  end

  def rand_n(n)
    randoms = Set.new
    loop do
      randoms << rand(86400000)
      return randoms.to_a if randoms.size >= n
    end
  end

  def next_state()
    @current_state = @current_state + 1
    if @current_state >= @states.length
      @current_state = 0
    end
    @states[@current_state]
  end
end

types = Array.new
types.push( EventType.new("novalabs_space", ["open", "closed"], 4) )
types.push( EventType.new("mongo", ["on", "off"], 10) )
types.push( EventType.new("table_saw", ["on", "off"], 4) )
types.push( EventType.new("lathe", ["on", "off"], 8) )
types.push( EventType.new("alarm", ["enabled", "disabled"], 6) )
types.push( EventType.new("front_door", ["open", "closed"], 30) )
types.push( EventType.new("shop_door", ["open", "closed"], 20) )

start_day = "2017-01-10T00:00:00.000Z"
start = Time.parse(start_day)
startEpochMillis = epoch_millis(start)
days = 60

for i in 0..(days - 1)
  day = to_time(startEpochMillis + (86400000 * i))
  for type in types
    events = type.events(day)
    for e in events
      response = RestClient.post('http://localhost:8080/event/events', e.to_json , {:content_type => :json})
      puts "#{e.to_json} - #{response.code}"
    end
  end
end


#response = RestClient.post('http://localhost:8080/event/events', e.to_json , {:content_type => :json})
