/*
 * Copyright 2015 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle

package object syntax {
  object all
      extends AngleSyntax
      with Base64WriterSyntax
      with BitmapSyntax
      with BlendSyntax
      with BufferedImageWriterSyntax
      with DebugSyntax
      with LayoutSyntax
      with NormalizedSyntax
      with PathSyntax
      with RendererSyntax
      with ShapeSyntax
      with SizeSyntax
      with StyleSyntax
      with TextSyntax
      with ClipItSyntax
      with ToPictureSyntax
      with TransformSyntax
      with TraverseSyntax
      with UnsignedByteSyntax
      with FileWriterSyntax
  object angle extends AngleSyntax
  object base64Writer extends Base64WriterSyntax
  object bitmap extends BitmapSyntax
  object blend extends BlendSyntax
  object bufferedImageWriter extends BufferedImageWriterSyntax
  object debug extends DebugSyntax
  object layout extends LayoutSyntax
  object normalized extends NormalizedSyntax
  object path extends PathSyntax
  object renderer extends RendererSyntax
  object shape extends ShapeSyntax
  object size extends SizeSyntax
  object style extends StyleSyntax
  object text extends TextSyntax
  object clipit extends ClipItSyntax
  object toPicture extends ToPictureSyntax
  object transform extends TransformSyntax
  object traverse extends TraverseSyntax
  object unsignedByte extends UnsignedByteSyntax
  object fileWriter extends FileWriterSyntax
}
