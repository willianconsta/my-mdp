from argparse import ArgumentParser
import os, sys

class Problem(object):
    def __init__(self, states, reward, cost, action_probabilities, discount_factor):
        self.states = states
        self.reward = reward
        self.cost = cost
        self.action_probabilities = action_probabilities
        self.discount_factor = discount_factor
        
def configure_args():
    """Define and validate the program arguments."""
    
    parser = ArgumentParser(description='Runs an MDP instance with the Value Iteration algorithm.')
    parser.add_argument("-problem", type=str, help = "Problem file")
    parser.add_argument("-error", type=float, help = "Error tolerance")

    args = parser.parse_args()

    if not os.path.isfile(args.problem):
        print "The file described in the 'problem' parameter does not exist or is not a valid file."
        sys.exit(-1)

    if not (0 < args.error < 1):
        print "The 'error' parameter must be between 0 (zero) and 1 (one)."
        sys.exit(-1)
        
    return args

def read_problem_data(problem_file):

    states = []
    reward = {}
    cost = {}
    action_probabilities = {}
    discount_factor = None
    initial_states = []
    goal_states = []
    
    with open(problem_file, "r") as f:       
        while True:
            line = f.readline()
            if not line: break
            
            if line == "":
                continue #empty line
            if line.startswith("states"):
                print "Loading 'states' section..."
            
                line = f.readline()
                line = line.replace(" ", "") #ignore spaces
                line = line.replace("\t", "") #ignore tabs
                #remove line break tokens from action name
                line = line.replace("\r", "")
                line = line.replace("\n", "")
                
                states.extend(line.split(","))
                
                line = f.readline()
                if not line.startswith("endstates"): return None
                
                print "'states' section loaded."
            elif line.startswith("action"):
                print "Loading 'action' section..."
            
                data = line.split(" ")
                action_name = data[1]
                #remove line break tokens from action name
                action_name = action_name.replace("\r", "")
                action_name = action_name.replace("\n", "")
            
                if not action_probabilities.has_key(action_name):
                    action_probabilities[action_name] = {}
            
                while True:
                    line = f.readline()
                    
                    if line.startswith("endaction"): 
                        print "'action' section loaded."
                        break
                    
                    line = line.replace("\t", "") #ignore tabs
                    
                    data = line.split(" ")
                    
                    from_state = data[0]
                    to_state = data[1]
                    probability = float(data[2])
                        
                    if not action_probabilities[action_name].has_key(from_state):
                        action_probabilities[action_name][from_state] = {}
                        
                    action_probabilities[action_name][from_state][to_state] = probability
            elif line.startswith("reward"):
                print "Loading 'reward' section..."
            
                while True:
                    line = f.readline()
                    
                    if line.startswith("endreward"): 
                        print "'reward' section loaded."
                        break
                    
                    line = line.replace("\t", "") #ignore tabs
                    
                    data = line.split(" ")
                    
                    state = data[0]
                    value = int(data[1])
                    
                    reward[state] = value
            elif line.startswith("cost"):
                print "Loading 'cost' section..."
            
                while True:
                    line = f.readline()
                    
                    if line.startswith("endcost"): 
                        print "'cost' section loaded."
                        break
                    
                    line = line.replace("\t", "") #ignore tabs
                    
                    data = line.split(" ")
                    
                    action = data[0]
                    value = int(data[1])
                    
                    cost[action] = value
            elif line.startswith("discount factor"):
                print "Loading 'discount factor' section..."
                
                data = line.split(" ")
                discount_factor = float(data[2])
                
                print "'discount factor' section loaded."
            elif line.startswith("initialstate"):
                print "Loading 'initialstate' section..."
            
                while True:
                    line = f.readline()
                    
                    if line.startswith("endinitialstate"): 
                        print "'initialstate' section loaded."
                        break
                    
                    line = line.replace("\t", "") #ignore tabs
                    
                    initial_states.append(line)
            elif line.startswith("goalstate"):
                print "Loading 'goalstate' section..."
            
                while True:
                    line = f.readline()
                    
                    if line.startswith("endgoalstate"): 
                        print "'goalstate' section loaded."
                        break
                    
                    line = line.replace("\t", "") #ignore tabs
                    
                    goal_states.append(line)
                

    #fill action probabilities
    for action_name in action_probabilities.keys():
        for from_state in action_probabilities[action_name].keys():
            for to_state in states:
                if not action_probabilities[action_name][from_state].has_key(to_state):
                    action_probabilities[action_name][from_state][to_state] = 0.0
                
    #validate MDP components
    
    error_message = "Error in problem file. %s"
    
    #empty arguments
    if len(states) == 0:
       print error_message%"Section [states] ommited."
       return None
    elif len(reward) == 0:
       print error_message%"Section [reward] ommited."
       return None
    elif len(cost) == 0:
       print error_message%"Section [cost] ommited."
       return None
    elif len(action_probabilities) == 0:
       print error_message%"Section [action] ommited."
       return None   
    elif discount_factor is None:
       print error_message%"Section [discount factor] ommited."
       return None   
               
    #missing reward for state
    if len(reward) != len(states):
        print error_message%("A reward must be defined for each state.")
        return None
        
    #invalid state in reward
    for state in reward.keys():
        if state not in states:
            print error_message%("Invalid state [%s] in reward fuction."%state)
            return None
    
    #missing cost for action
    if len(cost) != len(action_probabilities):
        print error_message%("A cost must be defined for each action.")
        return None
    
    #invalid action in cost
    for action in cost.keys():
        if action not in action_probabilities.keys():
            print error_message%("Invalid action [%s] in cost fuction."%action)
            return None
            
    #invalid state transition definition in action_probabilities
    for action_name in action_probabilities.keys():
        if len(states) != len(action_probabilities[action_name]):
            print error_message%("The action [%s] must define the transition probabilities for all states."%(action_name))
            return None
    
        for from_state in action_probabilities[action_name].keys():
            if from_state not in states:
                print error_message%("Invalid from-state [%s] in action [%s]."%(from_state, action_name))
                return None
                
            for to_state in action_probabilities[action_name][from_state].keys():
                if to_state not in states:
                    print error_message%("Invalid target state [%s] in [%s] transition in action [%s]."%(to_state, from_state, action_name))
                    return None
    
    #invalid transition probability in action_probabilities
    for action_name in action_probabilities.keys():
        for from_state in action_probabilities[action_name].keys():
            probability = sum(action_probabilities[action_name][from_state].values())
            
            if probability != 1.0:
                print error_message%("Invalid probability distribution on [%s] transition in action [%s]. The sum of all transitions in this state must be 1 (one)."%(from_state, action_name))
                return None
    
    #validate discount factor
    if discount_factor < 0 or discount_factor > 1:
        print error_message%"Invalid range for the discount factor. The value must be between 0 (zero) or 1 (one)."
        return None
    
    return Problem(states, reward, cost, action_probabilities, discount_factor)
    
def solve_mdp(problem_data, epsilon):
    policy = dict(map(lambda state : (state, None), problem_data.states))

    reward_heuristic = max(problem_data.reward.values())
    vupper = dict(map(lambda state : (state, reward_heuristic), problem_data.states))
    new_vupper = dict(map(lambda state : (state, None), problem_data.states))
    
    max_error = float("inf")
    
    iteration = 0
    
    while max_error > epsilon:
        #perform an iteration
        for state in problem_data.states:
            best_value = float("-inf")
            best_action = None
            
            for action in problem_data.action_probabilities.keys():        
                value = problem_data.reward[state] - problem_data.cost[action]
            
                pondered_sum = 0
                for next_state in problem_data.states:
                    pondered_sum += problem_data.action_probabilities[action][state][next_state] * vupper[next_state]
                    
                value += (problem_data.discount_factor * pondered_sum)
            
                if value > best_value:
                    best_value = value
                    best_action = action
                    
            new_vupper[state] = best_value
            policy[state] = best_action

        #compute error
        max_error = max(map(lambda state : abs(new_vupper[state] - vupper[state]), problem_data.states))
            
        #renew the vupper functions
        vupper = dict(new_vupper.iteritems())
        new_vupper = dict(map(lambda state : (state, None), problem_data.states))
        
        iteration = iteration + 1
        
    print "MDP solved at iteration [%d]"%iteration
    for state in sorted(vupper.keys()):
	    print str(state) + " -> " + str(vupper[state])
    return policy
    
def show_policy(policy):
    for state in sorted(policy.keys()):
        action = policy[state]
        print "%s = %s"%(state, action)

#Start the program execution
if __name__ == "__main__":
    args = configure_args()

    print "Loading problem file..."
    problem_data = read_problem_data(args.problem)
    print "Problem file loaded."
    print
    
    if problem_data is None:
        print "Corrupted problem file... Ending program."
        sys.exit(-1)
    
    print "Solving MDP..."
    policy = solve_mdp(problem_data, args.error)
    print "MDP solved."
    print
    
    print "Policy for [%s] problem..."%args.problem
    show_policy(policy)
