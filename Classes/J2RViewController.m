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
//  J2RViewController.m
//  J2Reversi
//

#import "J2RBoard.h"
#import "J2RSquare.h"
#import "J2RViewController.h"

@implementation J2RViewController {
  JOECommandInterface *_model;
}

- (void)viewDidLoad {
  [super viewDidLoad];
  self.background.image = [UIImage imageNamed:@"reversi.png"];
  self.blackScoreBackground.image = [UIImage imageNamed:@"black-score.png"];
  self.whiteScoreBackground.image = [UIImage imageNamed:@"white-score.png"];
	_model = [[JOECommandInterface alloc] init];
  CGRect frame = self.background.frame;
  CGFloat boardSize;
  CGRect boardFrame;
  if (CGRectGetWidth(frame) < CGRectGetHeight(frame)) {
    boardSize = CGRectGetWidth(frame);
    CGFloat y = (CGRectGetHeight(frame) - boardSize) / 2;
    boardFrame = CGRectMake(CGRectGetMinX(frame),
                             CGRectGetMinY(frame) + y,
                             boardSize,
                             boardSize);
  } else {
    boardSize = CGRectGetHeight(frame);
    CGFloat x = (CGRectGetWidth(frame) - boardSize) / 2;
    boardFrame = CGRectMake(CGRectGetMinX(frame) + x,
                             CGRectGetMinY(frame),
                             boardSize,
                             boardSize);
  }
  CGFloat squareSize = boardSize / 8;
  J2RBoard *board = [[J2RBoard alloc] initWithFrame:boardFrame
                                         squareSize:squareSize
                                              model:_model
                                      viewController:self];
  [self.view addSubview:board];
  [self.background removeFromSuperview];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
  if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) {
    return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
  } else {
    return YES;
  }
}

- (BOOL)gameFinished {
  return NO;
}

@end
