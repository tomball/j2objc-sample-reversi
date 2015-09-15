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
//  J2RSquare.m
//  J2Reversi
//

#import "J2RSquare.h"
#import "J2RViewController.h"
#import "CommandInterfaceListener.h"

@implementation J2RSquare

- (id)initWithFrame:(CGRect)frame
             column:(jint)column
                row:(jint)row
              model:(OECommandInterface *)model
     resultListener:(id<OECommandInterfaceListener>)listener {
  if (self = [super initWithFrame:frame]) {
    _column = column;
    _row = row;
    _model = model;
    _resultListener = listener;

    _blackImage = [UIImage imageNamed: @"black-stone.png"];
    _whiteImage = [UIImage imageNamed: @"white-stone.png"];
    _emptyImage = [UIImage imageNamed: @"empty-square.png"];
    _imageView = [[UIImageView alloc] initWithImage: _emptyImage];
    _imageView.alpha = 1.0;
    _imageView.center = CGPointMake(frame.size.width / 2, frame.size.height / 2);
    CGFloat pad = [UIDevice currentDevice].userInterfaceIdiom == UIUserInterfaceIdiomPhone ? 2 : 4;
    _imageView.bounds = CGRectMake(frame.origin.x + pad, frame.origin.y + pad,
                                   frame.size.width - (pad * 2), frame.size.height - (pad * 2));
    [self addSubview:_imageView];

    self.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"tile.png"]];
    [self update];
    UITapGestureRecognizer *tapRecognizer = [[UITapGestureRecognizer alloc]
                                             initWithTarget:self action:@selector(cellTapped:)];
    [self addGestureRecognizer:tapRecognizer];
  }
  return self;
}

- (void)update {
  // Model row and column numbers start with 1, not zero.
  jint column = _column + 1;
  jint row = _row + 1;

  // Returns 1 for white, 2 for black, and zero for empty.
  int state = [_model GetSquareWithInt:column withInt:row];

  if (_imageView.image == _emptyImage) {
    if (state != 0) {
      _imageView.image = state == 1 ? _whiteImage : _blackImage;
    }
  } else if (state == 1 && _imageView.image != _whiteImage) {
    [UIView transitionWithView:_imageView duration:0.5
                       options:UIViewAnimationOptionTransitionFlipFromRight animations:^{
                         _imageView.image = _whiteImage;
                       } completion:nil];
  } else if (state == 2 && _imageView.image != _blackImage) {
    [UIView transitionWithView:_imageView duration:0.5
                       options:UIViewAnimationOptionTransitionFlipFromRight animations:^{
                         _imageView.image = _blackImage;
                       } completion:nil];
  }
}

- (void)cellTapped:(UITapGestureRecognizer*)recognizer {
  // Model row and column numbers start with 1, not zero.
  jint column = _column + 1;
  jint row = _row + 1;

  if ([_model MakeMoveIsPossibleWithInt:column withInt:row]) {
    [_model MakeMoveWithInt:column withInt:row];
    [self update];
    [_model ComputeMoveWithOECommandInterfaceListener:_resultListener];
  } else {
    NSLog(@"invalid move: %d, %d", row, column);
  }
}

@end
