// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

//
//  J2RBoard.m
//  J2Reversi
//

#import "J2RBoard.h"
#import "J2RSquare.h"
#import "Move.h"

@implementation J2RBoard

@synthesize game;
@synthesize model;

- (id)initWithFrame:(CGRect)frame
         squareSize:(float)size
              model:(OECommandInterface *)model_
     viewController:(J2RViewController *)game_ {
  self = [super initWithFrame:frame];
  if (self) {
    self.game = game_;
    self.model = model_;
    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        J2RSquare* square =
            [[J2RSquare alloc] initWithFrame:CGRectMake(col * size + 1, row * size + 1,
                                                        size - 1, size - 1)
                                      column:col
                                         row:row
                                       model:model
                              resultListener:self];
        [self addSubview:square];
      }
    }
    self.backgroundColor = [UIColor clearColor];
    self.contentMode = UIViewContentModeScaleAspectFit;
    [model SetLevelWithInt:5];
    [self update];
  }
  return self;
}

- (void)update {
  game.blackScore.text = [NSString stringWithFormat:@"%d", [model GetScoreBlack]];
  game.whiteScore.text = [NSString stringWithFormat:@"%d", [model GetScoreWhite]];
  for (int row = 0; row < 8; row++) {
    for (int col = 0; col < 8; col++) {
      [self updateSquare:col withRow:row];
    }
  }
}

- (void)updateSquare:(NSInteger)column withRow:(NSInteger)row {
  int index = row * 8 + column;
  [[self.subviews objectAtIndex:index] update];
}

- (void)ComputationFinishedWithOEMove:(OEMove *)move {
  [self update];
  if ([move GetPlayer] == [model GetWhoseTurn]) {
    // No player move possible, so tell engine to move again.
    NSLog(@"no move available");
    if ([model ComputeMoveIsPossible]) {
      [model ComputeMoveWithOECommandInterfaceListener:self];
    }
  } else if ([model GetWhoseTurn] == 0) {
    NSLog(@"game over");
  }
}

@end
