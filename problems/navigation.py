from argparse import ArgumentParser
import os, sys

mark_robot_location = "robot-at-x%02dy%02d"
missing_robot_state = "broken-robot"

class State(object):
    def __init__(self, x, y):
        self.x = x
        self.y = y
        self.missing_robot = x is None and y is None

def configure_args():
    """Define and validate the program arguments."""
    
    parser = ArgumentParser(description='Generate an instance of the navigation MDP domain used in IPPC 2011.')
    parser.add_argument("-width", type=int, help = "Grid width")
    parser.add_argument("-height", type=int, help = "Grid height")
    parser.add_argument("-output", type=str, help = "Name of the domain output file.")

    args = parser.parse_args()

    if args.width < 2:
        print "The 'width' parameter must be equal or greater than 2 (two)"
        sys.exit(-1)
    elif args.height < 3:
        print "The 'height' parameter must be equal or greater than 3 (three)"
        sys.exit(-1)
    elif os.path.isfile(args.output):
        print "The file described in the 'output' parameter will be overriden."        

    return args

def get_state_string(x, y):
    return mark_robot_location %(x,y)

def enumerate_states(width, height):
    for x in xrange(1, width + 1):
        for y in xrange(1, height + 1):
            yield State(x, y)
            
    yield State(None, None)

def get_states(width, height):
    states = []

    for state in enumerate_states(width, height):
        if not state.missing_robot:
            states.append(get_state_string(state.x, state.y))
        else:
            states.append(missing_robot_state)
    
    return states

def get_existence_probability(x, max_x):
    first_point = 0.9
    last_point = 0.1
    return first_point + (x - 1) * ((last_point - first_point) / (max_x - 1))

def get_movenorth_action(width, height):
    action = []

    for x in xrange(1, width + 1):
        #first case, upper cells
        y = height
        from_state = get_state_string(x, y)
        to_state = get_state_string(x, y)
        action.append( (from_state, to_state, 1.0) )

        #second case, movement to upper cells
        y = height - 1
        from_state = get_state_string(x, y)
        to_state = get_state_string(x, y + 1)
        action.append( (from_state, to_state, 1.0) )
        
        #third case, another cells
        for y in xrange(1, height - 1):
            from_state = get_state_string(x, y)
            to_state = get_state_string(x, y + 1)
            probability = get_existence_probability(x, width)
            
            action.append( (from_state, to_state, probability) )
            action.append( (from_state, missing_robot_state, 1.0 - probability) )

    action.append( (missing_robot_state, missing_robot_state, 1.0) )
    return action

def get_movesouth_action(width, height):
    action = []

    for x in xrange(1, width + 1):
        #first case, lower cells
        y = 1
        from_state = get_state_string(x, y)
        to_state = get_state_string(x, y)
        action.append( (from_state, to_state, 1.0) )

        #second case, movement to lower cells
        y = 2
        from_state = get_state_string(x, y)
        to_state = get_state_string(x, y - 1)
        action.append( (from_state, to_state, 1.0) )
        
        #third case, another cells
        for y in xrange(3, height + 1):
            from_state = get_state_string(x, y)
            
            if x == width and y == height:
                #exception, goal state
                to_state = get_state_string(x, y)
                action.append( (from_state, to_state, 1.0) )
            else:
                to_state = get_state_string(x, y - 1)
                probability = get_existence_probability(x, width)
            
                action.append( (from_state, to_state, probability) )
                action.append( (from_state, missing_robot_state, 1.0 - probability) )

    action.append( (missing_robot_state, missing_robot_state, 1.0) )
    return action

def get_moveeast_action(width, height):
    action = []

    for y in xrange(1, height + 1):
        #first case, right corner cells
        x = width
        from_state = get_state_string(x, y)
        to_state = get_state_string(x, y)
        
        if y == 1 or y == height:
            action.append( (from_state, to_state, 1.0) )
        else:
            probability = get_existence_probability(x, width)
            action.append( (from_state, to_state, probability) )
            action.append( (from_state, missing_robot_state, 1.0 - probability) )
        
        for x in xrange(1, width):
            from_state = get_state_string(x, y)
            to_state = get_state_string(x + 1, y)
            
            #second case, movement in upper or lower cells
            if y == 1 or y == height:
                action.append( (from_state, to_state, 1.0) )
            else:
                #third case, another cells
                probability = get_existence_probability(x + 1, width)
                
                action.append( (from_state, to_state, probability) )
                action.append( (from_state, missing_robot_state, 1.0 - probability) )

    action.append( (missing_robot_state, missing_robot_state, 1.0) )
    return action

def get_movewest_action(width, height):
    action = []

    for y in xrange(1, height + 1):
        #first case, left corner cells
        x = 1
        from_state = get_state_string(x, y)
        to_state = get_state_string(x, y)
        
        if y == 1 or y == height:
            action.append( (from_state, to_state, 1.0) )
        else:
            probability = get_existence_probability(x, width)
            action.append( (from_state, to_state, probability) )
            action.append( (from_state, missing_robot_state, 1.0 - probability) )

        for x in xrange(2, width + 1):
            from_state = get_state_string(x, y)           

            if x == width and y == height:
                #exception, goal state
                to_state = get_state_string(x, y)
                action.append( (from_state, to_state, 1.0) )
            else:
                to_state = get_state_string(x - 1, y)
                
                #second case, movement in upper or lower cells
                if y == 1 or y == height:
                    action.append( (from_state, to_state, 1.0) )
                else:
                    #third case, another cells
                    probability = get_existence_probability(x - 1, width)
                            
                    action.append( (from_state, to_state, probability) )
                    action.append( (from_state, missing_robot_state, 1.0 - probability) )

    action.append( (missing_robot_state, missing_robot_state, 1.0) )
    return action

def get_actions(width, height):
    actions = {}

    actions["move-north"] = get_movenorth_action(width, height)
    actions["move-south"] = get_movesouth_action(width, height)
    actions["move-east"] = get_moveeast_action(width, height)
    actions["move-west"] = get_movewest_action(width, height)
    
    return actions

def get_reward(width, height):
    reward = []

    for x in xrange(1, width + 1):
        for y in xrange(1, height + 1):
            state = get_state_string(x, y)
            
            if x == width and y == height:
                reward.append( (state, 0) )
            else:
                reward.append( (state, -1) )
                
    reward.append( (missing_robot_state, -1) )
                
    return reward

def get_cost():
    cost = []

    cost.append( ( "move-north", 0 ) )
    cost.append( ( "move-south", 0 ) )
    cost.append( ( "move-east", 0 ) )
    cost.append( ( "move-west", 0 ) )
    
    return cost

def get_discount_factor():
    return 0.9
	
def get_initial_states(width, height):
	return [ get_state_string(width, 1) ] 

def get_goal_states(width, height):
	return [ get_state_string(width, height) ] 
	
def generate_domain_data(width, height):
    states = get_states(width, height)

    actions = get_actions(width, height)

    reward = get_reward(width, height)

    cost = get_cost()

    discount_factor = get_discount_factor()

    initial_states = get_initial_states(width, height)
	
    goal_states = get_goal_states(width, height)
	
    domain_data = []

    domain_data.append("states")  
    domain_data.append("\t" + ", ".join(states))
    domain_data.append("endstates")
    domain_data.append("")

    for action_name in actions.keys():
        domain_data.append("action %s" %action_name)
        for from_state, to_state, probability in actions[action_name]:
            domain_data.append("\t%s %s %f" %(from_state, to_state, probability))
        domain_data.append("endaction")
        domain_data.append("")

    domain_data.append("reward")
    for state, value in reward:
        domain_data.append("\t%s %d" %(state, value))
    domain_data.append("endreward")
    domain_data.append("")

    domain_data.append("cost")
    for action, value in cost:
        domain_data.append("\t%s %d" %(action, value))
    domain_data.append("endcost")
    domain_data.append("")

    domain_data.append("discount factor %f" %discount_factor)
    domain_data.append("")
	
    domain_data.append("initialstate")
    for state in initial_states:
        domain_data.append("\t%s" %state)
    domain_data.append("endinitialstate")
    domain_data.append("")
    
    domain_data.append("goalstate")
    for state in goal_states:
        domain_data.append("\t%s" %state)
    domain_data.append("endgoalstate")

    return domain_data

def write_domain_file(domain_data, output):
    """Given a domain data structured in lines, write a file in output path with these lines."""
    
    with open(output, "wb") as f:
        for line in domain_data:
            f.write(line)
            f.write("\r\n")

#Start the program execution
if __name__ == "__main__":
    args = configure_args()

    domain_data = generate_domain_data(args.width, args.height)

    write_domain_file(domain_data, args.output)