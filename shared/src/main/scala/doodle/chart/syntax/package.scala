package doodle
package chart

package object syntax extends ToPointSyntax
    //with ToRatioSyntax
    //with ToCategoricalSyntax
    with ChartSyntax
{
  object toPoint extends ToPointSyntax
  //object toRatio extends ToRatioSyntax
  //object toCategorical extends ToCategoricalSyntax
  object chart extends ChartSyntax
}
