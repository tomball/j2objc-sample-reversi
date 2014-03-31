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
//  J2RBoard.h
//  J2Reversi
//

#import <UIKit/UIKit.h>
#import "CommandInterface.h"
#import "CommandInterfaceListener.h"
#import "J2RViewController.h"

@interface J2RBoard : UIView <OECommandInterfaceListener>

@property (nonatomic, retain) J2RViewController *game;
@property (nonatomic, retain) OECommandInterface *model;

- (id)initWithFrame:(CGRect)frame
         squareSize:(float)size
              model:(OECommandInterface *)model
     viewController:(J2RViewController *)game;

- (void)update;

- (void)updateSquare:(NSInteger)column withRow:(NSInteger)row;

@end
