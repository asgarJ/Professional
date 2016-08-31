import random, time, sys

class Card(object):
    ''' 
        Card class stores content of card and provides manipulations with cards such as
        checking if it's Ace, evaluating value of it and generating a 52-card deck.
        
        Note that deciding value of Ace is not considered here, but inside Hand class,
        since the value of an Ace depends on other cards in hand.
    '''

    _content = None
    def __init__(self, *arg):
        if arg:
            self._content = arg[0]

    def __repr__(self):
        return str(self._content)

    def card_value(self):
        ''' Compute value of a card.
            '''
        s = self._content[1:]
        try:
            num = int(s)
            return num
        except ValueError:
            return 10

    def isAce(self):
        ''' Check if the card is Ace.'''
        s = self._content[1:]
        return s == 'A'

    def generate_a_deck(self):
        ''' Generate a deck of 52 cards.
        '''
        a = [str(i) for i in xrange(2, 11)]
        b = ['J', 'Q', 'K', 'A']
        a.extend(b)
        clubs =     [Card('C'+item) for item in a]
        diamonds =  [Card('D'+item) for item in a]
        hearts =    [Card('H'+item) for item in a]
        spades =    [Card('S'+item) for item in a]

        deck = [];  deck.extend(clubs); deck.extend(diamonds);  deck.extend(hearts);    deck.extend(spades)
        return deck

class DealingShoe(object):
    
    ''' DealingShoe class functions exactly as a card drawing machine. It can be initialised 
        with a number (of decks) which is one by default.
    '''
    
    cards = []      #   Cards in shoe machine
    N = 1           #   Number of decks
    _numOfDraws = 0

    def __init__(self, number=None):
        ''' Initialise and shuffle N number of decks.
            '''
        if number:
            self.N = int(number)
        deck = Card().generate_a_deck()
        for i in xrange(self.N):
            self.cards.extend(deck)
        random.shuffle(self.cards)

    def draw_a_card(self, chips):
        ''' Draw a card from the machine. Use the amount of chips for presentation of results.
            '''
        size = len(self.cards)
        if size > 0:
            drawn_card = self.cards.pop(0);     self._numOfDraws += 1
            return drawn_card

        else :
            print '\nGAME OVER..\tThere is no card left in the machine.'
            time.sleep(1)
            print '\n\t===  RESULTS  ===\n'
            
            if chips > 100:     print 'Congratulations..  Player earned %i more chips!\n' % (chips - 100)
            elif chips < 100:   print '\tPlayer lost %i chips\n' % (100 - chips)
            else :              print 'Player didn\'t lose or earn any chips. Better than loss.\n'

            sys.exit(0)

    def get_numberOf_draws(self):
        return self._numOfDraws